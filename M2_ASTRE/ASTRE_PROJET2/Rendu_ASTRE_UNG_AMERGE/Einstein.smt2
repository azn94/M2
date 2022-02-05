; Variable declarations
(declare-datatypes () ((Nationality Norwegian Brit Swede Dane German)))

; Pets
(declare-const Dogs Nationality)
(declare-const Cats Nationality)
(declare-const Birds Nationality)
(declare-const Horses Nationality)
(declare-const Fish Nationality)

(assert (distinct Dogs Cats Birds Horses Fish))

; Drinks
(declare-const Tea Nationality)
(declare-const Coffe Nationality)
(declare-const Milk Nationality)
(declare-const Water Nationality)
(declare-const Beer Nationality)

(assert (distinct Tea Coffe Milk Water Beer))

; Smoke Brand
(declare-const Blend Nationality)
(declare-const PallMall Nationality)
(declare-const Dunhill Nationality)
(declare-const Prince Nationality)
(declare-const BlueMaster Nationality)

(assert (distinct Blend PallMall Dunhill Prince BlueMaster))

; House number
(declare-const HouseOne Nationality)
(declare-const HouseTwo Nationality)
(declare-const HouseThree Nationality)
(declare-const HouseFour Nationality)
(declare-const HouseFive Nationality)

(assert (distinct HouseOne HouseTwo HouseThree HouseFour HouseFive))

; House color
(declare-const Red Nationality)
(declare-const Green Nationality)
(declare-const White Nationality)
(declare-const Yellow Nationality)
(declare-const Blue Nationality)

(assert (distinct Red Green White Yellow Blue))

; Function that give the left owner of the house situated in position index
(define-fun Left ((index Nationality)) (Nationality) (ite (= index HouseFive) HouseFour (ite (= index HouseFour) HouseThree (ite (= index HouseThree) HouseTwo (ite (= index HouseTwo) HouseOne HouseOne)))))

; Function that give the right owner of the house situated in position index
(define-fun Right ((index Nationality)) (Nationality) (ite (= index HouseOne) HouseTwo (ite (= index HouseTwo) HouseThree (ite (= index HouseThree) HouseFour (ite (= index HouseFour) HouseFive HouseFive)))))

; Constraints

; The Norwegian lives in the first house
(assert (= HouseOne Norwegian))

; The brit lives in the red house.
(assert (= Red Brit))

; The Swede keeps dogs as pets.
(assert (= Swede Dogs))

;  The Dane drinks tea.
(assert (= Dane Tea))

; The green house owner drinks coffee.
(assert (= Green Coffe))

; The person who smokes Pall Mall rears birds.
(assert (= PallMall Birds))

; The owner of the yellow house smokes Dunhill.
(assert (= Yellow Dunhill))

; The man living in the house right in the center drinks milk.
(assert (= HouseThree Milk))

; The green house is just on the left of the white house.
(assert (= Green (Left White)))

; The German smokes Prince.
(assert (= German Prince))

; The owner who smokes Blue Master drinks beer.
(assert (= BlueMaster Beer))

; The Norwegian lives next to the blue house.
(assert (and (not(= Norwegian Blue)) (xor (= Norwegian (Right Blue)) (= Norwegian (Left Blue)))))

; The man who smokes Blend lives next to the one who keeps cats.
(assert (and (not(= Blend Cats)) (xor (= Blend (Left Cats)) (= Blend (Right Cats)))))

; The man who smokes Blend has a neighbor who drinks water.
(assert (and (not(= Blend Water)) (xor (= Water (Left Blend)) (= Water (Right Blend)))))

; The man who keeps horses lives next to the man who smokes Dunhill.
(assert (and (not(= Horses Dunhill)) (xor (= Horses (Left Dunhill)) (= Horses (Right Dunhill)))))

; Solve
(check-sat)
(get-model)
