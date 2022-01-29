; Queens on different columns
(declare-const a Int)
(declare-const b Int)
(declare-const c Int)
(declare-const d Int)
(declare-const e Int)
(declare-const f Int)
(declare-const g Int)
(declare-const h Int)

; function that give the corresponding letter of a position in an alphabetical order (position up to 8): 
(define-fun letter ((index Int)) (Int) (ite (= index 1) a (ite (= index 2) b (ite (= index 3) c (ite (= index 4) d (ite (= index 5) e (ite (= index 6) f (ite (= index 7) g h))))))))


; function that give the corresponding position of a letter in an alphabetical order (letter up to h):
(define-fun index ((letter Int)) (Int) (ite (= letter a) 1 (ite (= letter b) 2 (ite (= letter c) 3 (ite (= letter d) 4 (ite (= letter e) 5 (ite (= letter f) 6 (ite (= letter g) 7 8))))))))

; function that test diagonal constraint : Queen_i - Queen_j != j +/- (i - j) for i,j in [1,8]
(define-fun diag ((col Int) (line Int)) (Bool) (and (not(= (index col) line)) (not(= (- col (letter line)) (- (index col) line))) (not(= (- col (letter line)) (- line (index col))))))

; Queens on different lines
(assert (distinct a b c d e f g h))

; Queens on a chess board 8x8
(assert (and (> a 0) (< a 9)))
(assert (and (> b 0) (< b 9)))
(assert (and (> c 0) (< c 9)))
(assert (and (> d 0) (< d 9)))
(assert (and (> e 0) (< e 9)))
(assert (and (> f 0) (< f 9)))
(assert (and (> g 0) (< g 9)))
(assert (and (> h 0) (< h 9)))

; ReineA diagonal constraint
(assert (diag a 2))
(assert (diag a 3))
(assert (diag a 4))
(assert (diag a 5))
(assert (diag a 6))
(assert (diag a 7))
(assert (diag a 8))

; ReineB diagonal constraint
(assert (diag b 1))
(assert (diag b 3))
(assert (diag b 4))
(assert (diag b 5))
(assert (diag b 6))
(assert (diag b 7))
(assert (diag b 8))

; ReineC diagonal constraint
(assert (diag c 1))
(assert (diag c 2))
(assert (diag c 4))
(assert (diag c 5))
(assert (diag c 6))
(assert (diag c 7))
(assert (diag c 8))

; ReineD diagonal constraint
(assert (diag d 1))
(assert (diag d 2))
(assert (diag d 3))
(assert (diag d 5))
(assert (diag d 6))
(assert (diag d 7))
(assert (diag d 8))

; ReineE diagonal constraint
(assert (diag e 1))
(assert (diag e 2))
(assert (diag e 3))
(assert (diag e 4))
(assert (diag e 6))
(assert (diag e 7))
(assert (diag e 8))

; ReineF diagonal constraint
(assert (diag f 1))
(assert (diag f 2))
(assert (diag f 3))
(assert (diag f 4))
(assert (diag f 5))
(assert (diag f 7))
(assert (diag f 8))

; ReineG diagonal constraint
(assert (diag g 1))
(assert (diag g 2))
(assert (diag g 3))
(assert (diag g 4))
(assert (diag g 5))
(assert (diag g 6))
(assert (diag g 8))

; Reineh diagonal constraint
(assert (diag h 1))
(assert (diag h 2))
(assert (diag h 3))
(assert (diag h 4))
(assert (diag h 5))
(assert (diag h 6))
(assert (diag h 7))

; Solve
(check-sat)
(get-model)
