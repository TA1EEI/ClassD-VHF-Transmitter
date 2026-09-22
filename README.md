# ClassD-RF-Transmitter: Mobile Audio-to-VHF Covert Channel

An experimental Android proof-of-concept demonstrating electromagnetic side-channel transmission (TEMPEST) via smartphone internal Class-D audio amplifiers.

By synthesizing inaudible 21 kHz ultrasonic audio pulses at 100% media volume, the app forces the internal Class-D H-Bridge driver to switch at high peak currents. The high-order PWM switching harmonics leak through unshielded speaker coils, creating a detectable RF comb spectrum around the 2-meter amateur radio band (144.000 MHz).

---

## Technical Overview

* **Mechanism:** High-current PWM switching modulation via standard Android `AudioTrack` API.
* **Carrier Frequency:** Comb spectrum across VHF (Centering around 144.000 MHz depending on the phone's internal PWM switching clock).
* **Acoustics:** Inaudible (21 kHz tone, 48 kHz sampling rate). No audible sound is emitted.
* **Modulation:** OOK (On-Off Keying) / CW Morse code.
* **Permissions Required:** Zero (No Root, No Camera, No Storage permissions required).

---

## Hardware Setup & Reception

### Transceiver / SDR Configuration:
1. **Frequency:** Tune your receiver to **144.000 MHz** (Search in 12.5 kHz / 25 kHz steps between 144.000 – 146.000 MHz if needed).
2. **Modulation:** **AM (Amplitude Modulation)** mode is essential. FM limiters strip amplitude envelopes.
3. **Squelch:** Set **Squelch = 0** (Open static noise) or hold the `MONI` button.
4. **Bandwidth:** Select **WIDE (25 kHz)** to account for clock jitter and spectral spreading.

### Smartphone Configuration:
1. Remove any thick conductive cases.
2. **Set Media Volume to 100%** (Amps will sleep/attenuate if volume is low).
3. Physically touch the transceiver's antenna tip to the **bottom speaker grill** (next to the USB-C port).

---



https://github.com/user-attachments/assets/75d38786-4f94-4583-80a4-9e3b7a3494c7

