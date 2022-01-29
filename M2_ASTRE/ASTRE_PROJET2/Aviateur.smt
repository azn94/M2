; Variable declarations
(declare-datatypes () ((Clients Underwood Vanderkook Wood Xing Young Zellman)))

; Villes
(declare-const Albany Clients)
(declare-const Birmingham Clients)
(declare-const Cincinatti Clients)
(declare-const Detroit Clients)
(declare-const ElPaso Clients)
(declare-const Fargo Clients)



; Raisons
(declare-const Halloween Clients)
(declare-const Idylle Clients)
(declare-const Jeu Clients)
(declare-const Kart Clients)
(declare-const Loisir Clients)
(declare-const Marriage Clients)

; Constraints
(assert (distinct Albany Birmingham Cincinatti Detroit ElPaso Fargo))
(assert (distinct Halloween Idylle Jeu Kart Loisir Marriage))

;1
(assert (not (= ElPaso Vanderkook)))
(assert (or (= ElPaso Idylle) (= ElPaso Marriage)))

;2
(assert (xor (= Birmingham Idylle) (= Birmingham Wood)))

;3
(assert (xor (= Albany Loisir) (= Albany Vanderkook)))

;4
(assert (= Young Kart))

;5
(assert (xor (= Cincinatti Underwood) (= Albany Underwood)))

;6
(assert (and (not(= Xing Fargo)) (not(= Vanderkook Fargo))))
(assert (or (= Fargo Idylle) (= Fargo Marriage)))

;7
(assert (or (= Detroit Halloween) (= Detroit Loisir)))

; Solve
(check-sat)
(get-model)
