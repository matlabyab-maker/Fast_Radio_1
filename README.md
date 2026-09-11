# Fast Radio — Update Base

This is the current base project for continuing development of **Fast Radio**.

## Current UI decisions
- No games.
- No window resizing / resize handles.
- Playback and recording buttons stay side-by-side, target size about 1 cm × 1 cm each.
- Two fixed station columns on the main screen:
  - **List 1 — Fast Radio internal radios**
  - **List 2 — Iran Radio**
- Global radio search remains separate and can search by country, name, language, tag/genre, bitrate and HTTPS.
- Translation window is intended to remain permanently open on the main screen.
- Subtitle sources open by clicking their network name/button; several can be open simultaneously; include Open All / Close All controls.
- Internet speed is shown in the radio pull-down/menu area.
- The calendar day number is shown in the pull-down/menu area.
- A lower radio information/warnings window reports playback engine reception, buffering, connection failures and other station problems.
- New information makes a warning lamp blink for 10 seconds.
- A row of 10 colored indicator lamps has a nearby run button.
- Playback engines are not to be removed merely to reduce APK size.

## Recording filenames
No custom AMR tags or metadata trailers are written.

The filename carries the requested information in this order:

`Country_Station_Genre_CountryCode_Date_Time.amr`

Examples:
- `Iran_BBC-Persian_News_Ir_2026-09-11_12-30-00.amr`
- `France_RFI-Persan_News_Fr_2026-09-11_13-15-00.amr`
- `USA_CNN_News_Us_2026-09-11_14-20-00.amr`

The helper is `RecordingFileNameBuilder.java`.

## Important implementation note
The current project is an update-ready foundation. Some requested features are represented in the UI/design plan but still require their full runtime implementation (for example direct radio-stream recording, subtitle feeds, and some fallback playback engines).

## Build and troubleshooting
GitHub Actions is configured to:
1. build the project;
2. save a detailed `build.log` even when the build fails;
3. upload the log as the `Fast-Radio-Build-Log` artifact;
4. upload the APK on successful builds.

## Next update targets
1. Finish playback engine fallback chain without removing Media3/ExoPlayer.
2. Finish real radio recording to `.amr` without microphone capture.
3. Implement the fixed main-screen translation window.
4. Implement subtitle windows and Open All / Close All.
5. Implement the 10-lamp control row.
6. Add live device internet speed and Persian-calendar day number.
7. Expand and validate the Iran Radio fixed station list.
8. Add global country-based search and station details.
