
trait MappingService[F[_]] {
  type Ship
  def loopkup(market: String, company: String, shipCode: String): F[Ship]
}
