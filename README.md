<p align="center">
  <a href="https://doi.org/10.5281/zenodo.22307837">
    <img src="https://zenodo.org/badge/1349900072.svg" alt="DOI" />
  </a>
  <a href="https://orcid.org/0009-0001-1628-2183">
    <img src="https://img.shields.io/badge/ORCID-0009--0001--1628--2183-A6CE39?logo=orcid&logoColor=white" alt="ORCID" />
  </a>
  <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License" />
</p>

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

## Author & Citation

**Efe Işık (TA1EEI)**  
* ORCID: [0009-0001-1628-2183](https://orcid.org/0009-0001-1628-2183)  
* Amateur Radio Callsign: TA1EEI  
* Email: [2007efeisik@gmail.com](mailto:2007efeisik@gmail.com)

If you use this proof-of-concept or refer to this research in academic/technical work, please cite it as:

```bibtex
@software{isik2026classd_transmitter,
  author       = {Işık, Efe},
  title        = {ClassD-VHF-Transmitter: Mobile Audio-to-VHF Covert Channel},
  year         = {2026},
  publisher    = {Zenodo},
  doi          = {10.5281/zenodo.22307837},
  url          = {[https://doi.org/10.5281/zenodo.22307837](https://doi.org/10.5281/zenodo.22307837)}
}

