; Variable declarations
(declare-datatypes () ((Villes Albany Birmingham Cincinatti Detroit ElPaso Fargo)))
(declare-datatypes () ((Clients Underwood Vanderkook Wood Xing Young Zellman)))
(declare-datatypes () ((Raisons Halloween Idylle Jeu Kart Loisir Marriage)))

; Trajets
(declare-const Trajet (Array Villes Clients Raisons))

;1
(assert (exists ((c Clients)) (and (not(= c Vanderkook)) (or (= (select Trajets ElPaso c) Idylle) (= (select Trajets ElPaso c) Marriage)))))

;2
(assert (exists ((c Clients)) (exists ((r Raisons)) (ite (= c Wood) (and (not(= r Idylle)) (= (select Trajets Birmingham Wood) r)) (= (select Trajets Birmingham c) Idylle) ))))

;3
(assert (exists ((c Clients)) (exists ((r Raisons)) (ite (= c Vanderkook) (and (not(= r Loisir)) (= (select Trajets Albany Vanderkook) r)) (= (select Trajets Albany c) Loisir)))))

;4
(assert (exists ((v Villes)) (= (select Trajets v Young) Kart)))

;5
(assert (exists ((r Raisons)) (or (= (select Trajets Albany Underwood) r)  (= (select Trajets Cincinatti Underwood) r))))

;6a
(assert (exists ((r Raisons)) (and (not(= (select Trajets Fargo Xing) r)) (not(= (select Trajets Fargo Vanderkook) r)))))

;6b
(assert (exists ((c Clients)) (or (= (select Trajets Fargo c) Idylle) (= (select Trajets Fargo c) Marriage))))

;7
(assert (exists ((c Clients)) (or (= (select Trajets Detroit c) Halloween) (= (select Trajets Detroit c) Loisir))))


; Solve
(check-sat)
(get-model)
