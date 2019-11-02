---?color=linear-gradient(180deg, white 75%, black 25%)
@title[Title slide]
@snap[midpoint span-100 h1]
## Exploring strong-typed functions in Scala
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

@snap[north-west]
## Introduction
@snapend

@snap[midpoint span-100]
One of the biggest benefits FP is that it lets us reason about functions by looking at their *type signature*. However...
@snapend

---
@title[Problem]

@snap[north-west]
## Introduction
@snapend

@snap[midpoint span-100]
**We often end up with weakly-typed functions**
@snapend

---

@snap[north-east]
### Let the code do the talking!
@snapend

@snap[midpoint span-110]
```scala zoom-12
trait MappingService[F[_]] {
  type Ship
  def lookup(
    market: String,
    company: String,
    shipCode: String
  ): F[Ship]
}
```
@snapend

@snap[south span-100 text-gray text-14]
@[3-7, zoom-14](Do you see any problems with this function?)
@snapend

---

@title[Problems]

@snap[north-east]
### Issues
@snapend

@snap[west span-35]
```scala zoom-12
def lookup(
 market: String,
 company: String,
 shipCode: String
): F[Ship]
```
@snapend

@snap[east span-65]
@ul[list-spaced-bullets black text-09]
- Easy to:
    - confuse the order of the parameters
    - feed our function with invalid data
@ulend
@snapend

---

@title[Params dissection]

@snap[north-east]
### Params dissection
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

@snap[south span-100 text-gray text-14]
@[2-2, zoom-14](Finite and fixed number of possible values {au,de,it,br,nl,ru,us,fr})
@[3-3, zoom-14](Finite/open set of values. Three lowercase letters {msc, ncl, aid,...})
@[4-4, zoom-14](Non empty)
@snapend

---

@title[Params: market]

@snap[north-east]
### Params: market
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
  
  implicit val CirceEncoder: Encoder[Market] = ...
  
  implicit val CirceDecoder: Decoder[Market] = ...
   
}
```
@snapend

@snap[south span-100 text-red text-18]
@[19-19, zoom-18](Params: company & shipCode ???)
@snapend

---

@title[Value classes]

@snap[north-west]
## Value classes
@snapend

@snap[midpoint span-100]
Wrap a single primitive type and extend the AnyVal abstract class to avoid some runtime costs.
@snapend

@snap[south span-100]
[VALUE CLASSES AND UNIVERSAL TRAITS @fa[external-link]](https://docs.scala-lang.org/overviews/core/value-classes.html)
@snapend

---

@title[Value classes 1]

@snap[north-west]
### Solution 1: Value classes (1)
@snapend

@snap[midpoint]
```scala zoom-16
final case class CompanyCode(val value: String) extends AnyVal
final case class ShipCode(val value: String) extends AnyVal

def lookup(
  market: MarketCode,
  company: CompanyCode,
  shipCode: ShipCode
): F[Ship]

...

val ship = def lookup(
            market = Germany,
            company = CompanyCode("hal"),
            shipCode = ShipCode("E45AK")
           ): F[Ship]
```
@snapend

@snap[south span-100 text-gray text-14]
@[1-3, zoom-14](Defining the value classes for CompanyCode and ShipCode)
@[4-9, zoom-14](New signature of the lookup)
@[10-16, zoom-14](Example of usage)
@snapend

---

@title[Value classes 2a]

@snap[north-west]
### Solution 1: Value classes (2)
@snapend

@snap[west span-40]
@ul[list-spaced-bullets black text-09]
Yey! We can no longer confuse the order of the parameters
Or... can we?
@ulend
@snapend

---

@title[Value classes 2b]

@snap[north-west]
### Solution 1: Value classes (2)
@snapend

@snap[west span-40]
@ul[list-spaced-bullets black text-09]
Yey! We can no longer confuse the order of the parameters
Or... can we?
@ulend
@snapend

@snap[east span-60]
```scala zoom-16
val ship1 = def lookup(
            market = Germany,
            company = CompanyCode("E45AK"),
            shipCode = ShipCode("hal")
           ): F[Ship]
           
val ship2 = def lookup(
            market = Germany,
            company = CompanyCode("ahls"),
            shipCode = ShipCode("")
           ): F[Ship]
           
...           
```
@snapend

@snap[south span-100 text-gray text-14]
@[1-5, zoom-14](We still can mess the parameters)
@[6-10, zoom-14](Validation is missing)
@[12-12, zoom-18](The compiler doesn’t help us and **that is all we need**)
@snapend

---

@title[Value classes 3]

@snap[north-west]
### Solution 1: Value classes (3)
@snapend

@snap[midpoing span-100]
@ul[list-spaced-bullets black text-08]
A workaround is to make the case class constructors private and provide smart constructors / factory methods
@ulend
@snapend

---

@title[Value classes 4]

@snap[north-west]
#### Solution 1: Value classes + smart constructors (4)
@snapend

@snap[west span-100]
```scala zoom-16
final case class CompanyCode private(val value: String) extends AnyVal
final case class ShipCode private(val value: String) extends AnyVal
           
def createShipCode(value: String): Option[ShipCode] =
  if (value.nonEmpty) ShipCode(value).some else none[ShipCode]

def createPolarCompanyCode(value: String): Option[CompanyCode] =
  if ("""[a-z]{3}""".r matches value) CompanyCode(value).some
  else none[CompanyCode]

...

(
  createCompanyCode("hal"),
  createShipCode("E45AK")
).mapN {
  case (companyCode, shipCode) =>
    lookup(Germany, companyCode, shipCode)
}           
```
@snapend

@snap[south span-100 text-gray text-14]
@[1-3, zoom-14](Make case class constructors private)
@[4-11, zoom-14](Provide functions that create a validated value)
@[13-19, zoom-14](Perform a ship lookup with valid parameters...)
@snapend

---

@title[Value classes 5a]

@snap[north-west]
#### Solution 1: Value classes + smart constructors (5)
@snapend

@snap[west span-30]
@ul[list-spaced-bullets black small]
Yey! We can't confuse the order of the parameters neither provide invalid input
Or... can we?
@ulend
@snapend

---

@title[Value classes 5b]

@snap[north-west]
#### Solution 1: Value classes + smart constructors (5)
@snapend

@snap[west span-30]
@ul[list-spaced-bullets black small]
Yey! We can't confuse the order of the parameters neither provide invalid input
Or... can we?
@ulend
@snapend

@snap[east span-70]
```scala zoom-16
(
  createCompanyCode("hal"),
  createShipCode("E45AK")
).mapN {
  case (companyCode, shipCode) =>
    lookup(Germany, companyCode, shipCode.copy(""))
}  
           
```
@snapend

@snap[south span-100 text-gray text-14]
@[6-6, zoom-14](We are using case classes; the copy method is still there :()
@snapend

---

@title[Value classes 6]

@snap[north-west]
#### Solution 1: Value classes + factory methods (6)
@snapend

@snap[west span-100]
```scala zoom-16
final case class CompanyCode private(val value: String) extends AnyVal {
  def copy(s: String = this.value) = CompanyCode(s)  
}

object CompanyCode {
  def apply(value: String): Option[CompanyCode] =
    if ("""[a-z]{3}""".r matches value) CompanyCode(value).some
    else none[CompanyCode]
}           
```
@snapend

@snap[south span-100 text-gray text-14]
@[1-4, zoom-14](Copy should be replaced)
@[5-10, zoom-14](Replace the generated apply method within the companion object)
@snapend

---

@title[Value classes 6]

@snap[north-west]
#### Solution 1: Value classes + factory methods (6)
@snapend

@snap[west span-100]
```scala zoom-16
final case class ShipCode private(val value: String) extends AnyVal {
  private def copy() = ()  
}

object ShipCode {
  def apply(value: String): Option[CompanyCode] =
    if (value.nonEmpty) ShipCode(value).some else none[ShipCode]  
}
```
@snapend

@snap[south span-100 text-gray text-14]
@[1-4, zoom-14](Another possibility is to make the copy method private)
@[5-8, zoom-14](Replace the generated apply method within the companion object)
@snapend
