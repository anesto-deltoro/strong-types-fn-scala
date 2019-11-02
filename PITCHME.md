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
@snap[west-north span-40]
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
    - Newtypes
    - Refinement types
    - Enforcing types*
- Examples: GreenGinza
- Conclusions
@ulend
@snapend

---
@title[Problem]

@snap[north-east]
## Introduction
@snapend

@snap[midpoint span-100]
One of the biggest benefits FP is that it lets us reason about functions by looking at their *type signature*. However...
@snapend

---
@title[Problem]

@snap[north-east]
## Introduction
@snapend

@snap[midpoint span-100]
**We often end up with weakly-typed functions**
@snapend

---

@snap[north-east]
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

@snap[north-east]
## Issues
@snapend

@snap[west span-40]
```scala zoom-12
def lookup(
 market: String,
 company: String,
 shipCode: String
): F[Ship]
```
@snapend

@snap[east span-50]
@ul[list-spaced-bullets black text-09]
- Easy to:
    - confuse the order of the parameters
    - feed our function with invalid data
@ulend
@snapend

---

@title[Params detailed]

@snap[north-east]
## Params detailed
@snapend

@snap[midpoint span-40]
```scala zoom-16
def lookup(
 market: String,
 company: String,
 shipCode: String
): F[Ship]
```
@snapend

@snap[south span-100 text-gray text-18]
@[2-2, zoom-18](Finite and fixed number of possible values {de,it,br,nl,ru,us,fr})
@[3-3, zoom-18](Three lowercase letters, finite set of values but variable per CruiseLine. Polar {hal,sea,ccl,pcl,cun}; NCL {regent,oceania})
@[4-4, zoom-18](Non empty)
@snapend

---

@title[Params: market]

@snap[north-east]
## Params: market
@snapend

@snap[east]
```scala zoom-16
sealed abstract class Market(val code: String) extends EnumEntry
object Market extends Enum[Market] {
  case object Australia extends Market("au")
  case object Brazil extends Market("br")
  case object Germany extends Market("de")
  case object France extends Market("fr")
  case object Italy extends Market("it")
  case object Netherlands extends Market("nl")
  case object Russia extends Market("ru")
  case object UnitedStates extends Market("us")

  override def values: immutable.IndexedSeq[Market] = findValues
  
  def byCode(code: String): Option[Market] = values.find(_.code == code)
  
  implicit val CirceEncoder: Encoder[Market] = Encoder.encodeString.contramap[Market](_.code)
  
  implicit val CirceDecoder: Decoder[Market] = ...
}
```
@snapend