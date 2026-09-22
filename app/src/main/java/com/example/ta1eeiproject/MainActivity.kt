package com.example.ta1eeiproject

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.sin

class MainActivity : AppCompatActivity() {

    // Zamanlama parametreleri (Milisaniye)
    private val UNIT_MS = 80L
    private val DIT_MS = UNIT_MS
    private val DAH_MS = UNIT_MS * 3
    private val INTRA_CHAR_GAP_MS = UNIT_MS
    private val CHAR_GAP_MS = UNIT_MS * 3
    private val WORD_GAP_MS = UNIT_MS * 7

    // Ses ve Amfi Parametreleri
    private val SAMPLE_RATE = 48000
    private val ULTRASONIC_FREQ = 21000.0 // 21 kHz (Kulağın duymadığı bölge)

    private var audioTrack: AudioTrack? = null
    private var ditBuffer = ShortArray(0)
    private var dahBuffer = ShortArray(0)
    private var silenceDitBuffer = ShortArray(0)

    private var transmitJob: Job? = null
    private var isTransmitting = false

    private lateinit var etMessage: EditText
    private lateinit var btnTransmit: Button
    private lateinit var tvStatus: TextView

    private val morseAlphabet = mapOf(
        'A' to ".-",    'B' to "-...",  'C' to "-.-.",  'D' to "-..",
        'E' to ".",     'F' to "..-.",  'G' to "--.",   'H' to "....",
        'I' to "..",    'J' to ".---",  'K' to "-.-",   'L' to ".-..",
        'M' to "--",    'N' to "-.",    'O' to "---",   'P' to ".--.",
        'Q' to "--.-",  'R' to ".-.",   'S' to "...",   'T' to "-",
        'U' to "..-",   'V' to "...-",  'W' to ".--",   'X' to "-..-",
        'Y' to "-.--",  'Z' to "--..",  '1' to ".----", '2' to "..---",
        '3' to "...--", '4' to "....-", '5' to ".....", '6' to "-....",
        '7' to "--...", '8' to "---..", '9' to "----.", '0' to "-----",
        ' ' to "/"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etMessage = findViewById(R.id.etMessage)
        btnTransmit = findViewById(R.id.btnTransmit)
        tvStatus = findViewById(R.id.tvStatus)

        // Ultrasonik PCM ses tamponlarını bellekte önceden üret
        prepareAudioBuffers()
        initAudioTrack()

        tvStatus.text = "Ready! (Set your phones volume to %100)"

        btnTransmit.setOnClickListener {
            if (isTransmitting) {
                stopTransmission()
            } else {
                val text = etMessage.text.toString().trim().uppercase()
                if (text.isNotEmpty()) {
                    startTransmission(text)
                } else {
                    Toast.makeText(this, "Mesaj boş olamaz!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun prepareAudioBuffers() {
        val ditSamples = (SAMPLE_RATE * (DIT_MS / 1000.0)).toInt()
        val dahSamples = (SAMPLE_RATE * (DAH_MS / 1000.0)).toInt()

        ditBuffer = ShortArray(ditSamples)
        dahBuffer = ShortArray(dahSamples)
        silenceDitBuffer = ShortArray(ditSamples) { 0 }

        // 21 kHz Sinüs dalgası (Maksimum genlik: 32767 - Amfiyi tam güçle sürmek için)
        for (i in 0 until ditSamples) {
            val angle = 2.0 * PI * i * ULTRASONIC_FREQ / SAMPLE_RATE
            ditBuffer[i] = (sin(angle) * Short.MAX_VALUE).toInt().toShort()
        }

        for (i in 0 until dahSamples) {
            val angle = 2.0 * PI * i * ULTRASONIC_FREQ / SAMPLE_RATE
            dahBuffer[i] = (sin(angle) * Short.MAX_VALUE).toInt().toShort()
        }
    }

    private fun initAudioTrack() {
        val bufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()
    }

    private fun startTransmission(message: String) {
        isTransmitting = true
        btnTransmit.text = "STOP"

        transmitJob = lifecycleScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                tvStatus.text = "Class-D PWM Active: $message"
            }

            val track = audioTrack ?: return@launch

            for (char in message) {
                if (!isTransmitting) break

                if (char == ' ') {
                    delay(WORD_GAP_MS)
                    continue
                }

                val morseCode = morseAlphabet[char]
                if (morseCode != null) {
                    for (symbol in morseCode) {
                        if (!isTransmitting) break

                        when (symbol) {
                            '.' -> {
                                // 21 kHz Ultrasonik Patlama (Dit)
                                track.write(ditBuffer, 0, ditBuffer.size, AudioTrack.WRITE_BLOCKING)
                            }
                            '-' -> {
                                // 21 kHz Ultrasonik Patlama (Dah)
                                track.write(dahBuffer, 0, dahBuffer.size, AudioTrack.WRITE_BLOCKING)
                            }
                        }
                        // Semboller arası sessizlik payı
                        track.write(silenceDitBuffer, 0, silenceDitBuffer.size, AudioTrack.WRITE_BLOCKING)
                    }
                    // Karakterler arası ek bekleme
                    delay(CHAR_GAP_MS - INTRA_CHAR_GAP_MS)
                }
            }

            withContext(Dispatchers.Main) {
                stopTransmission()
                tvStatus.text = "Completed!"
            }
        }
    }

    private fun stopTransmission() {
        isTransmitting = false
        transmitJob?.cancel()
        btnTransmit.text = "Start"
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTransmission()
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }
}