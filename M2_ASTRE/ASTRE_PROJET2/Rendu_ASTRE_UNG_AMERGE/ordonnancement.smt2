; Exercice : ordonnancement
; Variable declarations

; job0 = [(0,1), (1,2), (2,2)];
(declare-const t00 Int)
(declare-const t01 Int)
(declare-const t02 Int)
; job1 = [(0,2), (2,1), (1,3)];
(declare-const t10 Int)
(declare-const t11 Int)
(declare-const t12 Int)
; job2 = [(1,3),(2,3)];
(declare-const t20 Int)
(declare-const t21 Int)


; Constraints
(assert (and (>= t00 0) (>= t01 0) (>= t02 0) (>= t10 0) (>= t11 0) (>= t12 0) (>= t20 0) (>= t21 0)))

; - each task in a job must start only after the previous task has been completed.
(assert (and (<= (+ t00 1)  t01) (<= (+ t01 2)  t02)))
(assert (and (<= (+ t10 2)  t11) (<= (+ t11 1)  t12))) 
(assert (and (<= (+ t20 3)  t21)))

; - a task cannot be paused - the time it takes to complete cannot be divided. 
; - the work stations can only work on one task.

; Machine 0 : only 2 tasks {t00, t10}
(assert (or
  (<= (+ t00 3) t10)
  (<= (+ t10 2) t00)
))


; Machine 1: three tasks {t01, t12, t20}
; so 6 possible sequences
(assert (or
  (and (<= (+ t01 2) t12) (<= (+ t12 3) t20) )
  (and (<= (+ t01 2) t20) (<= (+ t20 3) t12) )
  (and (<= (+ t12 3) t01) (<= (+ t01 2) t20) )
  (and (<= (+ t12 3) t20) (<= (+ t20 3) t01) )
  (and (<= (+ t20 3) t01) (<= (+ t01 2) t12) )
  (and (<= (+ t20 3) t12) (<= (+ t12 3) t01) )
))

; Machine 2 : three tasks {t02, t11, t21}
; so 6 possible sequences again
(assert (or
  (and (<= (+ t02 2) t11) (<= (+ t11 1) t21) )
  (and (<= (+ t02 2) t21) (<= (+ t21 3) t11) )
  (and (<= (+ t11 1) t02) (<= (+ t02 2) t21) )
  (and (<= (+ t11 1) t21) (<= (+ t21 3) t02) )
  (and (<= (+ t21 3) t02) (<= (+ t02 2) t11) )
  (and (<= (+ t21 3) t11) (<= (+ t11 1) t02) )
))

(minimize (+ t00 t01 t02 t10 t11 t12 t20 t21))

; Solve
(check-sat)
(get-model)
