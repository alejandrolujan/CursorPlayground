package secretsanta

/**
 * Data model for Secret Santa configuration and results
 */

/**
 * Represents a single exclusion pair - two people who should not be paired together
 */
case class ExclusionPair(person1: String, person2: String)

/**
 * Root configuration class containing participants and exclusions
 */
case class SecretSantaConfig(
  participants: List[String],
  exclusions: List[ExclusionPair]
)

/**
 * Represents a Secret Santa pairing (giver → receiver)
 */
case class Pairing(giver: String, receiver: String)

