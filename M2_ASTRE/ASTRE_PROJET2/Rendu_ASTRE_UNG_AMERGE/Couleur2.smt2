; Variable declarations
(declare-datatypes () ((Color Red Green Blue)))
(declare-const A Color)
(declare-const B Color)
(declare-const D Color)
(declare-const E Color)
(declare-const F Color)
(declare-const I Color)
(declare-const N Color)
(declare-const S Color)
(declare-const L Color)

; Constraints
(assert (not (= F E)))
(assert (not (= F I)))
(assert (not (= F S)))
(assert (not (= F D)))
(assert (not (= F B)))
(assert (not (= S I)))
(assert (not (= S D)))
(assert (not (= D B)))
(assert (not (= D N)))
(assert (not (= B N)))

(assert (not (= L B)))
(assert (not (= L D)))
(assert (not (= L F)))

; Solve
(check-sat)
(get-model)
