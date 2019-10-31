---?color=linear-gradient(180deg, white 75%, black 25%)
@title[Title slide]
@snap[midpoint span-100 h1]
## Strong-typed functions in Scala
@snapend

@snap[south span-100 text-white small]
com.dreamlines.connectivity
03.11.2019
@snapend

---?color=linear-gradient(90deg, white 35%, #5384AD 65%)
@title[Agenda]
@snap[west-north span-40 h3]
## Agenda
@snapend

@snap[west-south span-50]
![IMAGE](assets/img/presentation.png)
@snapend

@snap[east span-100]
@ul[list-spaced-bullets text-white text-09]
- Problem
- Alternatives
    - Value classes
    - @newrecord
    - Refined
    - Enforcing types
- Examples: GreenGinza
- Conclusions
    
@ulend
@snapend

---
@title[Problem]

@snap[north-east h4-black]
## Introduction
@snapend

@snap[midpoint span-100]
One of the biggest benefits FP is that it lets us reason about functions by looking at their *type signature*. However...
@snapend

---
@title[Problem]

@snap[north-east h4-black]
## Introduction
@snapend

@snap[midpoint span-100]
**We often end up with weakly-typed functions**
@snapend

---

@snap[north-east h4-black]
## Let the code do the talking!
@snapend

@snap[midpoint span-110]
```scala zoom-12
trait MappingService[F[_]] {
  type Ship
  def lookup(market: String, company: String,
    shipCode: String): F[Ship]
}
```
@snapend

@snap[south span-100 text-gray text-08]
@[3-4, zoom-18](Do you see any problems with this function?)
@snapend

---

@title[Problems]

@snap[north-east h4-black]
## Issues
@snapend

@snap[west span-40]
```scala zoom-12
def lookup(market: String,
 company: String,
 shipCode: String): F[Ship]
```
@snapend

@snap[east span-60]
@ul[list-spaced-bullets black text-09]
- Easy to:
    - confuse the order of the parameters
    - feed our function with invalid data
@ulend
@snapend
