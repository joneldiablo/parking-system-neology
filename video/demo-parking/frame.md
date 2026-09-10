---
colors:
  bg: "#0B1220"
  surface: "#131C2E"
  surface_2: "#1C2942"
  ink: "#EAF1FC"
  ink_muted: "#8FA6C4"
  accent: "#3B9DFD"
  accent_soft: "#9CCBFF"
  success: "#2BD9A8"
  danger: "#FF6B6B"
  line: "rgba(234,241,252,0.14)"
typography:
  display:
    family: "Archivo Black"
    weight: 400
  heading:
    family: "Montserrat"
    weight: 700
  body:
    family: "Montserrat"
    weight: 400
  mono:
    family: "JetBrains Mono"
    weight: 400
spacing:
  unit: 8
  margin: 96
radius:
  card: 20
  pill: 999
components:
  chip:
    background: "rgba(59,157,253,0.14)"
    border: "1px solid rgba(59,157,253,0.45)"
    radius: 999
    color: "#9CCBFF"
  lower_third:
    background: "rgba(11,18,32,0.86)"
    border_left: "4px solid #3B9DFD"
    radius: 12
---

# Frame design spec — Parking System demo

## Overview

Dark tech system mined from the product's own brand: the admin runs on Bootstrap
blue (`#0d6efd`) and the kiosk on a near-black surface with a blue scan line, so
the video's identity is **deep navy ground + electric blue accent + a green
"access granted" secondary** (taken straight from the kiosk's success state).

The footage is light (white admin cards). It is always presented inside a dark
frame so the eye reads "the product, on our stage" — never full-bleed edge to
edge except in the kiosk scene, where the kiosk's own dark card sits naturally.

## Palette

- **bg** `#0B1220` — deep navy, never pure black; subtle radial glows of accent.
- **surface** `#131C2E` / `#1C2942` — cards, lower-thirds, panels.
- **ink** `#EAF1FC`, **ink_muted** `#8FA6C4` — body text and captions.
- **accent** `#3B9DFD` — the one hue; all chrome, rules, chips, highlights.
- **success** `#2BD9A8` — the kiosk "acceso concedido" green.
- **danger** `#FF6B6B` — denied states only.
- Neutrals are tinted toward the accent hue (cool blue-gray), never dead gray.

## Typography

- **Archivo Black** (display, weight 400 only) — hook and closing statements.
- **Montserrat** 700/900 for headings and lower-third titles; 400 for body.
- **JetBrains Mono** for technical labels, section numbers, plate codes, endpoint
  strings, and timestamps (tabular-nums on any data).
- Body 22–26px min; headlines 64px+; feed-sized only if a vertical cut is made.
- Light-on-dark: raise line-height ~0.08 and keep body weight ~350–400.

## The Frame

- **Focal:** the product footage (screen recording) — always the brightest thing.
- **Edge anchors:** top-left mono section tag (`01 · KIOSKO QR`); bottom mono
  progress rule across the frame.
- **Supporting detail:** a lower-third plate (surface + 4px accent left border)
  carrying the current action; chips for facts (`JWT`, `AES/GCM`, `ROLES`).
- **Background:** navy + two low-opacity accent radial glows with a slow ambient
  breath, plus a faint 1px grid at ~5% ink for depth behind designed frames.

## Composition Rules

- Footage sits in a rounded surface card (radius 20) with a 1px `line` border and
  a soft accent glow; it never bleeds full-frame except the kiosk dark card.
- Designed frames use a single expressive move, then hold; one accent hue only.
- Every scene carries 2–5 ambient decoratives sharing ONE breathing motion.
- Lower-thirds: slide-in from the left with the accent border drawing on, hold,
  slide-out — one per beat, timed to the narration.
- Transitions: hard cuts inside the admin walkthrough; crossfade or wash between
  designed sections. No gratuitous wipes.
- Section numbers are mono, `0N ·`, always top-left.
- No gradient text, no left-stripe spam beyond the one lower-third accent border.
