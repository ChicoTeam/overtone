(ns
  ^{:doc "Functions to help work with musical time."
     :author "Jeff Rose"}
  overtone.music.rhythm
  (:use
    [overtone.sc core]
    [overtone time-utils]))

; Rhythm

; * a resting heart rate is 60-80 bpm
; * around 150 induces an excited state


; A rhythm system should let us refer to time in terms of rhythmic units like beat, bar, measure,
; and it should convert these units to real time units (ms) based on the current BPM and signature settings.

(defn beat-ms
  "Convert 'b' beats to milliseconds at the given 'bpm'."
  [b bpm] (* (/ 60000.0 bpm) b))

;(defn bar-ms
;  "Convert b bars to milliseconds at the current bpm."
;  ([] (bar 1))
;  ([b] (* (bar 1) (first @*signature) b)))

; A metronome is a linear function that given a beat count returns the time in milliseconds.
;
; tpb = ticks-per-beat
(defn metronome
  "A metronome is a beat management function.  Tell it what BPM you want,
  and it will output beat timestamps accordingly.  Call the returned function
  with no arguments to get the next beat number, or pass it a beat number
  to get the timestamp to play a note at that beat.

  (def m (metronome 128))
  (m)          ; => <current beat number>
  (m 200)      ; => <timestamp of beat 200>
  (m :bpm 140) ; => set bpm to 140"
  [bpm]
  (let [start   (atom (now))
        tick-ms (atom (beat-ms 1 bpm))]
    (fn
      ([] (inc (long (/ (- (now) @start) @tick-ms))))
      ([beat] (+ (* beat @tick-ms) @start))
      ([_ bpm]
       (let [tms (beat-ms 1 bpm)
             cur-beat (long (/ (- (now) @start) @tick-ms))
             new-start (- (now) (* tms cur-beat))]
         (reset! tick-ms tms)
         (reset! start new-start))
       [:bpm bpm]))))

(comment defprotocol IMetronome
  (start [this])
  (stop  [this])
  (beat  [this])
  (tick  [this])
  (bpm   [this] [this bpm]))

;== Grooves
;
; A groove represents a pattern of velocities and timing modifications that is
; applied to a sequence of notes to adjust the feel.
;
; * swing
; * jazz groove, latin groove
; * techno grooves (hard on beat one)
; * make something more driving, or more layed back...
