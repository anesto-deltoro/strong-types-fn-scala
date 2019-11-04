---?color=linear-gradient(180deg, white 75%, black 25%)
@title[Title slide]

@snap[midpoint span-100]
## Exploring </br> strong-typed functions in Scala
@snapend

@snap[south span-100 text-white small]
com.dreamlines.connectivity
06.11.2019
@snapend

---?color=linear-gradient(90deg, white 35%, #5384AD 65%)
@title[Agenda]

@snap[west-north span-40]
## Agenda
@snapend

@snap[west-south span-50]
![IMAGE](assets/img/presentation.png)
@snapend

@snap[east span-50]
@ul[list-spaced-bullets text-white text-09]
- Problem
- Possible solutions
  - Value classes
  - Newtypes
  - Refinement types
- Conclusions
@ulend
@snapend

---
@title[Introduction 1]

@snap[north-west]
## Introduction
@snapend

@snap[midpoint span-100 text-14]
One of the biggest benefits FP is that it lets us reason about functions by looking at their *type signature*.

In practice, however...
@snapend

---
@title[Introduction 2]

@snap[north-west]
## Introduction
@snapend

@snap[midpoint span-100 text-16]
**We often end up with </br> weakly-typed functions**
@snapend

---
@title[Problem]

@snap[north-east]
#### Let the code do the talking!
@snapend

@snap[midpoint span-110]
```scala zoom-12
trait MappingService[F[_]] {

  type Ship
  
  def lookup(
    marketCode: String,
    companyCode: String,
    shipCode: String
  ): F[Ship]
  
}
```
@snapend

@snap[south span-100 text-gray text-14]
@[5-9, zoom-14](Do you see any problems with this function?)
@snapend

---?color=linear-gradient(90deg, white 40%, #5384AD 60%)
@title[Problem: Issues]

@snap[north-east]
#### Issues
@snapend

@snap[west span-35]
```scala zoom-12
def lookup(
  marketCode: String,
  companyCode: String,
  shipCode: String
): F[Ship]
```
@snapend

@snap[east span-37]
@ul[list-spaced-bullets black text-09]
- Easy to:
  - confuse the order of the parameters
  - feed our function with invalid data
@ulend
@snapend

---
@title[Problem: params dissection]

@snap[north-east]
#### Params dissection
@snapend

@snap[midpoint span-40]
```scala zoom-16
def lookup(
  marketCode: String,
  companyCode: String,
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
@title[Problem: params abstractions]

@snap[north-east]
#### Params abstractions
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

@snap[south span-100 text-gray text-14]
@[1-18, zoom-14](marketCode: approach recently introduced by Chris in DragonFly)
@[19-19, zoom-14](companyCode & shipCode?)
@snapend

---
@title[Approach 0: Type aliases]

@snap[north-west]
## Approach 0: @css[text-06 text-black](Type aliases)
@snapend

@snap[west span-100]
```scala zoom-16
object greenginza {

    type CompanyCode = String
    type ShipCode = String

}

def lookup(market: MarketCode, companyCode: CompanyCode,
  shipCode: ShipCode): F[Ship]
  
...

lookup(marketCode = Germany, companyCode = "hal", shipCode = "E45AK")
lookup(marketCode = Germany, companyCode = "E45AK", shipCode = "hal")
lookup(marketCode = Germany, companyCode = "ahls", shipCode = "")
```
@snapend

@snap[south span-100 text-gray text-14]
@[1-7, zoom-14](Defining the type aliases for CompanyCode and ShipCode)
@[8-10, zoom-14](Signature of the lookup)
@[13-13, zoom-14](Example of usage Ok)
@[14-14, zoom-14](We can mess the parameters)
@[15-15, zoom-14](We can provide invalid input)
@snapend

---
@title[Approach 1: value classes]

@snap[north-west]
## Approach 1: @css[text-06 text-black](Value classes)
@snapend

@snap[midpoint span-100]
@ul[list-spaced-bullets black text-09]
- Wrap a single primitive type and extend the AnyVal abstract class to avoid some runtime costs.
- One use case is to get the type safety of a data type without the runtime allocation overhead.
@ulend

@snapend

@snap[south span-100]
[VALUE CLASSES AND UNIVERSAL TRAITS @fa[external-link]](https://docs.scala-lang.org/overviews/core/value-classes.html)
@snapend

---
@title[Approach 1: Value classes 1]

@snap[north-west]
#### Approach 1: Value classes (1)
@snapend

@snap[west span-100]
```scala zoom-16
final case class CompanyCode(val value: String) extends AnyVal
final case class ShipCode(val value: String) extends AnyVal

def lookup(
  marketCode: MarketCode,
  companyCode: CompanyCode,
  shipCode: ShipCode
): F[Ship]

...

val ship = lookup(
  marketCode = Germany,
  companyCode = CompanyCode("hal"),
  shipCode = ShipCode("E45AK")
)
```
@snapend

@snap[south span-100 text-gray text-14]
@[1-3, zoom-14](Defining the value classes for CompanyCode and ShipCode)
@[4-9, zoom-14](New signature of the lookup)
@[10-16, zoom-14](Example of usage)
@snapend

---
@title[Approach 1: Value classes 2]

@snap[north-west]
#### Approach 1: Value classes (2)
@snapend

@snap[west span-100]
@ul[list-spaced-bullets black text-09]
- Yey! We can't confuse the order of the parameters
- Or... can we?
@ulend
@snapend

---
@title[Approach 1: Value classes 3]

@snap[north-west]
#### Approach 1: Value classes (3)
@snapend

@snap[west span-100]
```scala zoom-16
val ship1 = lookup(
  marketCode = Germany,
  companyCode = CompanyCode("E45AK"),
  shipCode = ShipCode("hal")
)
           
val ship2 = lookup(
  marketCode = Germany,
  companyCode = CompanyCode("ahls"),
  shipCode = ShipCode("")
)
      
```
@snapend

@snap[south span-100 text-gray text-14]
@[1-6, zoom-14](We still can mess the parameters)
@[7-11, zoom-14](We still can create invalid instances)
@[12-12, zoom-14](The compiler doesn’t help us and **that is all we need**)
@snapend

---
@title[Approach 1: Value classes 4]

@snap[north-west]
#### Approach 1: Value classes (4)
@snapend

@snap[midpoing span-100]
@ul[list-spaced-bullets black text-08]
A workaround is to make the case class constructors private and provide smart constructors / factory methods
@ulend
@snapend

---
@title[Approach 1a: Value classes+smart constructors 5]

@snap[north-west]
#### Approach 1a: Value classes+smart constructors (1)
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
@[4-8, zoom-14](Provide functions that create a validated value)
@[9-16, zoom-14](Perform a ship lookup with valid parameters...)
@snapend

---
@title[Approach 1a: Value classes+smart constructors 6]

@snap[north-west]
#### Approach 1a: Value classes+smart constructors (2)
@snapend

@snap[west span-100]
@ul[list-spaced-bullets black small]
- Yey! We can't confuse the order of the parameters neither provide invalid input
- Or... can we?
@ulend
@snapend

---
@title[Approach 1a: Value classes+smart constructors 7]

@snap[north-west]
#### Approach 1a: Value classes+smart constructors (3)
@snapend

@snap[west span-100]
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
@title[Approach 1b: Value classes+factory methods 8]

@snap[north-west]
#### Approach 1b: Value classes+factory methods (1)
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
@[2-2, zoom-14](Copy should be replaced)
@[7-9, zoom-14](Replace the generated apply method within the companion object)
@snapend

---
@title[Approach 1b: Value classes+factory methods 9]

@snap[north-west]
#### Approach 1b: Value classes+factory methods (2)
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

(
  CompanyCode("hal"),
  ShipCode("E45AK")
).mapN {
  case (companyCode, shipCode) =>
    lookup(Germany, companyCode, shipCode)
}  
```
@snapend

@snap[south span-100 text-gray text-14]
@[2-2, zoom-14](Another possibility is to make the copy method private)
@[6-7, zoom-14](Replace the generated apply method within the companion object)
@[9-16, zoom-14](Example of usage)
@snapend

---
@title[Value classes caveats]

@snap[north-west]
## Value classes caveats
@snapend

@snap[midpoint span-100]
@ul[list-spaced-bullets black text-07]
- Value classes are fine if used with caution but they have limitations and performance issues.
- The language can not guarantee that these primitive type wrappers won’t actually allocate more memory.
- Is actually instantiated when:
  - a value class is treated as another type.
  - a value class is assigned to an array.
  - doing runtime type tests, such as pattern matching.
- A lot of boilerplate!!!
@ulend
@snapend

@snap[south span-100 text-08]
[VALUE CLASSES AND UNIVERSAL TRAITS @fa[external-link]](https://docs.scala-lang.org/overviews/core/value-classes.html)
@snapend

---
@title[Approach 2]

@snap[north-west]
## Approach 2: @css[text-06 text-black](Library newtype)
@snapend

@snap[midpoint span-100]
@ul[list-spaced-bullets black text-07]
- Haskell language provides a newtype keyword for creating new types from existing ones without runtime overhead.
- The newtype scala library gives us zero-cost wrappers with no runtime overhead relying on macros.
  - libraryDependencies += "io.estatico" %% "newtype" % "0.4.3"
  - addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
- Newtypes will eventually be replaced by opaque types (Dotty, a subset in 2.14)
@ulend
@snapend

@snap[south span-100 text-08]
[SCALA-NEWTYPE @fa[external-link]](https://github.com/estatico/scala-newtype)
@snapend

---
@title[Approach 2a: scala newtype case class 1]

@snap[north-west]
#### Approach 2a: Scala newtype case class (1)
@snapend

@snap[midpoint span-100]
```scala zoom-16
import io.estatico.newtype.macros.newtype

package object greenginza {

  @newtype case class CompanyCode(toStr: String)
   
}
```
@snapend

@snap[south span-100 text-gray text-14]
@[1-7, zoom-14](Defining the newtype for CompanyCode using *case class*)
@[5-5, zoom-14](Using case class gives us a smart constructor (apply) that will accept an String value and return the newtype CompanyCode)
@[5-5, zoom-14](Get an accessor extension method to get the underlying String (private*))
@snapend

---
@title[Approach 2a: scala newtype case class 2]

@snap[north-west]
#### Approach 2a: Scala newtype case class (2)
@snapend

@snap[midpoint span-100]
```scala zoom-14
package object greenginza {

  type CompanyCode = CompanyCode.Type
  
  object CompanyCode {
    type Repr = Int
    type Base = Any { type CompanyCode$newtype }
    trait Tag extends Any
    type Type <: Base with Tag

    def apply(x: String): CompanyCode = x.asInstanceOf[CompanyCode]

    implicit final class Ops$newtype(val $this$: Type) extends AnyVal {
      def toStr: String = $this$.asInstanceOf[String]
    }
  }
}
```
@snapend

@snap[south span-100 text-gray text-14]
@[5-16, zoom-16](Generated code looks similar to this)
@[11-11, zoom-16](Smart constructor (apply) that accept an String value and return the newtype CompanyCode)
@[13-15, zoom-16](Accessor method for the underlying value)
@snapend

---
@title[Approach 2a: scala newtype case class 3]

@snap[north-west]
#### Approach 2a: Scala newtype case class (3)
@snapend

@snap[midpoint span-100]
```scala zoom-14
import io.estatico.newtype.macros.newtype

package object greenginza {
  @newtype case class CompanyCode(toStr: String)
  @newtype case class ShipCode(toStr: String)
}
...
(
  CompanyCode("hal"),
  ShipCode("E45AK")
).mapN {
  case (companyCode, shipCode) =>
    lookup(Germany, companyCode, shipCode)
}
```
@snapend

@snap[south span-100 text-gray text-14]
@[4-5, zoom-14](Define newtypes case classes for CompanyCode and ShipCode)
@[8-14, zoom-14](Perform a ship lookup with valid parameters...)
@snapend

---
@title[Approach 2a: scala newtype case class 4]

@snap[north-west]
#### Approach 2a: Scala newtype case class (4)
@snapend

@snap[west span-100]
@ul[list-spaced-bullets black small]
- Yey! We can't confuse the order ...
- Or... can we?
@ulend
@snapend

---
@title[Approach 2a: scala newtype case class 5]

@snap[north-west]
#### Approach 2a: Scala newtype case class (5)
@snapend

@snap[east span-70]
```scala zoom-16
(
  CompanyCode("E45AK"),
  ShipCode("")
).mapN {
  case (companyCode, shipCode) =>
    lookup(Germany, companyCode, shipCode)
} 
    
```
@snapend

@snap[south span-100 text-gray text-14]
@[2-2, zoom-14](We can still mess the parameters :()
@[3-3, zoom-14](We can still create invalid instances :()
@snapend

---
@title[Approach 2b: scala newtype case class 1]

@snap[north-west]
#### Approach 2b: Scala newtype class (2)
@snapend

@snap[midpoint span-100]
```scala zoom-16
import io.estatico.newtype.macros.newtype

package object greenginza {

  @newtype class ShipCode(val toStr: String)
  
  object ShipCode {
  
    def fromString(str: String): Option[ShipCode] = {
      if (str.nonEmpty) Some(str.coerce)
      else none[ShipCode]
    }
    
  }
}
```
@snapend

@snap[south span-100 text-gray text-14]
@[5-5, zoom-16](Defining the newtype for CompanyCode using *class*)
@[5-5, zoom-16](Using class will not generate a smart constructor (apply))
@[5-5, zoom-16](Accessor method for the underlying value can be achieved with *val*)
@[9-12, zoom-16](We can specify our own smart constructor)
@[10-10, zoom-16](Use the .coerce extension method to cast to the newtype)
@snapend

---
@title[Approach 2b: scala newtype case class 2]

@snap[north-west]
#### Approach 2b: Scala newtype class (2)
@snapend

@snap[midpoint span-100]
```scala zoom-14
package object greenginza {
  @newtype class CompanyCode(val toStr: String)
  @newtype class ShipCode(val toStr: String)

  object CompanyCode {
    def fromString(str: String): Option[CompanyCode] = ...
  }
  object ShipCode {
    def fromString(str: String): Option[ShipCode] = ...
  }
}
...
(
  CompanyCode.fromString("hal"), ShipCode.fromString("E45AK")
).mapN {
  case (companyCode, shipCode) =>
    lookup(Germany, companyCode, shipCode)
}
```
@snapend

@snap[south span-100 text-gray text-14]
@[2-3, zoom-14](Define newtypes classes for CompanyCode and ShipCode)
@[5-10, zoom-14](Specify our own smart constructors)
@[13-18, zoom-14](Perform a ship lookup with valid parameters)
@snapend

---
@title[Scala newtypes caveats]

@snap[north-west]
## Newtypes caveats
@snapend

@snap[midpoint span-100]
@ul[list-spaced-bullets black text-07]
- Newtypes help us tremendously in our strongly-typed functions quest. But...
- It requires smart constructors to validate input data, which adds boilerplate.
- We end-up with a bittersweet feeling... 
@ulend
@snapend

@snap[south span-100 text-08]
[SCALA-NEWTYPE @fa[external-link]](https://github.com/estatico/scala-newtype)
@snapend

---
@title[Refinement types]

@snap[north-west]
## Approach 3: @css[text-06 text-black](Refinement types)
@snapend

@snap[midpoint span-100]
@ul[list-spaced-bullets black text-07]
- It started as a port of the refined Haskell library
- Is a Scala library for refining types with *type-level predicates* which constrain the set of values described by the refined type.
- Refinement types allow us to validate data in **compile time** as well as in runtime.
- Multiple optional extensions and library integrations.
    - libraryDependencies += "eu.timepit" %% "refined" % "0.9.10"
@ulend
@snapend

@snap[south span-100 text-08]
[SIMPLE REFINEMENT TYPES FOR SCALA @fa[external-link]](https://github.com/fthomas/refined)
@snapend

---
@title[Refinement types]

@snap[north-west]
#### Refinement types: basics
@snapend

@snap[midpoint span-100]
@ul[list-spaced-bullets black text-07]
- refinement type = @css[text-green](base type) + @css[text-blue](predicate)
- values of a refinement type = @css[text-green](all values of the base type) that @css[text-blue](satisfy the predicate)
- Examples
  - ShipCode = @css[text-green text-12](String) + @css[text-blue text-12]((∀ s => s.nonEmpty))
  - CompanyCode = @css[text-green text-12](String) + @css[text-blue text-12]((∀ s => """[a-z]{3}""".r matches s))
@ulend
@snapend

---
@title[Approach 3: Refinement types 1]

@snap[north-west]
#### Approach 3: Refinement types (1)
@snapend

@snap[midpoint span-100]
```scala zoom-14
import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.types.string.MatchesRegex

package object greenginza {
  type CompanyCode = String Refined MatchesRegex[W.'"[a-z]{3}"'.T]]
  type ShipCode = NonEmptyString
}

def lookup(market: MarketCode, company: CompanyCode,
  shipCode: ShipCode): F[Ship]
...
lookup(Germany, "hal", "E45AK")
lookup(Germany, "E45AK", "hal")
lookup(Germany, "hal", "")
lookup(Germany, "ahls", "E45AK")
```
@snapend

@snap[south span-100 text-gray text-14]
@[1-5, zoom-14](Required imports)
@[6-10, zoom-14](Defining refinement types for CompanyCode and ShipCode)
@[7-7, zoom-14](Some predicates require encoding literals at the type-level (shapeless witness macro is used))
@[11-12, zoom-14](lookup function)
@[14-14, zoom-14](Compiles)
@[15-15, zoom-14](Error)
@[16-16, zoom-14](Error)
@[17-17, zoom-14](Error)
@snapend

---

![Cartoon](https://media.giphy.com/media/m8crpzTJFRDPhqqhXJ/giphy.mp4)

---
@title[Approach 3: Refinement types 2]

@snap[north-west]
#### Approach 3: Refinement types (2)
@snapend

@snap[west span-100]
@ul[list-spaced-bullets black text-07]
- Yey!... we are done!
- Or... are we?
@ulend
@snapend

---
@title[Approach 3: Refinement types 3]

@snap[north-west]
#### Approach 3: Refinement types (3)
@snapend

@snap[west span-100]
```scala zoom-14
import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString

package object greenginza {
  type CompanyCode = String Refined MatchesRegex[W.'"[a-z]{3}"'.T]]
}

def lookup(
  market: MarketCode,
  company: CompanyCode,
  shipCode: NonEmptyString,
  cabinCode: NonEmptyString
): F[Cabin]
...
lookup(Germany, "hal", "A1", "E45AK")
lookup(Germany, "hal", "E45AK", "A1")
```
@snapend

@snap[south span-100 text-gray text-14]
@[12-13, zoom-14](Same refined type for more than param :()
@[16-17, zoom-14](Again we can confuse the order of the parameters :()
@snapend

---
@title[Final approach: Refinement types + newtypes]

@snap[north-west]
#### Final approach: Refinement types + newtypes
@snapend

@snap[east span-100]
```scala zoom-14
import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.types.string.MatchesRegex

package object greenginza {
  type CompanyCode = String Refined MatchesRegex[W.'"[a-z]{3}"'.T]]
  @newtype case class ShipCode(value: NonEmptyString)
  @newtype case class CabinCode(value: NonEmptyString)
}

def lookup(
  market: MarketCode,
  company: CompanyCode,
  shipCode: ShipCode,
  cabinCode: CabinCode
): F[Cabin]
...
lookup(Germany, "hal", ShipCode("E45AK"), CabinCode("A1"))
```
@snapend

@snap[south span-100 text-gray text-14]
@[7-7, zoom-14](Refined type with regex based validation)
@[8-9, zoom-14](These two types share the same validation rule (we use refinement types)
@[8-9, zoom-14](but since they represent different concepts, we create a newtype for each of them))
@[12-17, zoom-14](Final signature of the lookup function)
@[19-19, zoom-14](Strong-typed scala function with compile type validation!)
@snapend

---
@title[Refinement types extended]

@snap[north-west]
#### Refinement types: extended
@snapend

@snap[east span-100]
```scala zoom-14
import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.types.string.MatchesRegex
import eu.timepit.refined.api.RefType

package object greenginza {
  type CompanyCode = String Refined MatchesRegex[W.'"[a-z]{3}"'.T]]
  type ShipCode = NonEmptyString
}
val (company, ship) = "hal" -> "E45AK"
(
  RefType.applyRef[[greenginza.CompanyCode]](company).toOpt,
  RefType.applyRef[[greenginza.ShipCode]](shipCode).toOpt
).mapN {
  case (companyCode, shipCode) =>
    lookup(Germany, companyCode, shipCode)
}
```
@snapend

@snap[south span-100 text-gray text-14]
@[1-6, zoom-14](Required imports)
@[8-9, zoom-14](Refinement types for CompanyCode and ShipCode)
@[11-11, zoom-14](Most of the time with don't work with literals)
@[13-14, zoom-14](Enforcing validation!)
@[17-17, zoom-14](Strong-typed scala function with compile type validation!)
@snapend

---
@title[Refinement types: combining predicates]

@snap[north-west]
#### Refinement types: combining predicates
@snapend

@snap[midpoint span-100]
@ul[list-spaced-bullets black text-07]
- The library comes with a LOT of predefined predicates for: boolean, char, collection, numeric and generic [CHECK HERE @fa[external-link]](https://github.com/fthomas/refined) 
- Simple predicates can be combined using the boolean predicates into a more complex predicate
- Example: TwitterUserName
  - Usernames containing the words 'Twitter' or 'Admin' cannot be claimed.
  - Usernames cannot be longer than 15 characters. (not including '@').
  - Usernames can only contain alphanumeric characters (letters A-Z, numbers 0-9) with the exception of underscores.
@ulend
@snapend

---
@title[Refinement types: TwitterUsername]

@snap[north-west]
#### Refinement types: complex predicate
@snapend

@snap[west span-100]
```scala zoom-14
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.{AllOf, Not, Or, And}
import eu.timepit.refined.string.{MatchesRegex, StartsWith}
import eu.timepit.refined.W
import eu.timepit.refined.char.LetterOrDigit
import eu.timepit.refined.collection.{NonEmpty, MaxSize, Tail}
import eu.timepit.refined.generic.Equal
import shapeless.::
import shapeless.HNil

type TwitterUserName = String Refined AllOf[
  StartsWith[W.'"@"'.T] ::
  MaxSize[W.'16'.T] ::
  Not[MatchesRegex[W.'"(?i:.*twitter.*)"'.T]] ::
  Not[MatchesRegex[[W.'"(?i:.*admin.*)"'.T]] ::
  Tail[Or[LetterOrDigit, Equal[W.''_''.T]]] ::
  HNil
]
```
@snapend

---
@title[Refinement types: circe]

@snap[north-west]
#### Refinement types: circe example
@snapend

@snap[west span-100]
```scala zoom-14
import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.MatchesRegex
import eu.timepit.refined.api.RefType
import io.circe.{Decoder, DecodingFailure, Encoder}

package object greenginza {
  type CompanyCode = String Refined MatchesRegex[W.'"[a-z]{3}"'.T]]
  
  implicit val CirceEncoder: Encoder[CompanyCode] =
    Encoder.encodeString.contramap[[CompanyCode]](_.value)
    
  implicit val CirceDecoder: Decoder[CompanyCode] = Decoder.instance {
    c => Decoder.decodeString(c).flatMap(s =>
      RefType.applyRef[[CompanyCode]](s) match {
        case Right(v) => v.asRight[DecodingFailure]
        case Left(err) => err.asLeft[CompanyCode]
      }
    )
}
```
@snapend

---?image=assets/img/presenter.jpg
@title[Conclusions]

@snap[north span-100 h2-white]
## Conclusions
@snapend

@snap[west span-60]
@ul[list-spaced-bullets text-white text-07]
- We should be able to reason about functions by looking at their *type signature*.
- We should rely more on the help of the compiler.
- Aim for type safety without runtime allocation overhead.
- Refinement types + newtypes are an excellent start.
@ulend
@snapend

---

![Cartoon](https://media.giphy.com/media/cLlVn5zC5UOSmQZKJ7/giphy.mp4)

