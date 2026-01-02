package secretsanta

import scala.util.Random

/**
 * Pairing algorithm for Secret Santa with exclusion constraints
 */
object PairingGenerator {

  private val MaxAttempts = 1000

  /**
   * Generates a valid Secret Santa pairing that respects exclusion constraints
   * 
   * @param config The Secret Santa configuration with participants and exclusions
   * @return Either an error message or a list of valid pairings
   */
  def generatePairings(config: SecretSantaConfig): Either[String, List[Pairing]] = {
    val participants = config.participants
    val exclusions = config.exclusions

    // Edge case: Empty participants
    if (participants.isEmpty) {
      return Left("No participants provided")
    }

    // Edge case: Need at least 2 participants for Secret Santa
    if (participants.length < 2) {
      return Left("Need at least 2 participants for Secret Santa")
    }

    // Edge case: Check for duplicate participants
    val duplicates = participants.groupBy(identity).filter(_._2.length > 1).keys.toList
    if (duplicates.nonEmpty) {
      return Left(s"Duplicate participants found: ${duplicates.mkString(", ")}")
    }

    // Edge case: Validate that all exclusion pairs reference existing participants
    val participantSet = participants.toSet
    val invalidExclusions = exclusions.filter { pair =>
      !participantSet.contains(pair.person1) || !participantSet.contains(pair.person2)
    }
    if (invalidExclusions.nonEmpty) {
      val invalidPairs = invalidExclusions.map(p => s"${p.person1}-${p.person2}").mkString(", ")
      return Left(s"Exclusions reference non-existent participants: $invalidPairs")
    }



    // Create a set of exclusion pairs for fast lookup (both directions)
    val exclusionSet = exclusions.flatMap { pair =>
      Set(
        (pair.person1, pair.person2),
        (pair.person2, pair.person1)
      )
    }.toSet

    // Edge case: Early detection of impossible scenarios
    // If we have only 2 participants and they exclude each other, it's impossible
    // Check this BEFORE the general "too many exclusions" check for a more specific error message
    if (participants.length == 2) {
      val p1 = participants.head
      val p2 = participants(1)
      if (exclusionSet.contains((p1, p2)) || exclusionSet.contains((p2, p1))) {
        return Left(s"Impossible pairing: With only 2 participants (${p1} and ${p2}), they cannot exclude each other")
      }
    }

    // Edge case: Check if too many exclusions make pairing impossible
    // For n participants, we need at least n valid directed pairs to form a cycle
    // Each exclusion removes 2 directed pairs (bidirectional)
    val totalPossiblePairs = participants.length * (participants.length - 1)
    val excludedPairs = exclusionSet.size
    val remainingPairs = totalPossiblePairs - excludedPairs
    
    // We need at least n pairs (one per participant) to form a cycle
    // Skip this check for 2 participants since we already handled that case above
    if (participants.length > 2 && remainingPairs < participants.length) {
      return Left(s"Too many exclusions: Only $remainingPairs valid pairs remain, but need at least ${participants.length} to form a cycle")
    }

    // Try to generate a valid pairing
    var attempts = 0
    while (attempts < MaxAttempts) {
      val shuffled = Random.shuffle(participants)
      val pairings = createCircularPairings(shuffled)
      
      if (isValidPairing(pairings, exclusionSet)) {
        return Right(pairings)
      }
      
      attempts += 1
    }

    Left(s"Could not generate valid pairing after $MaxAttempts attempts. The exclusions may make pairing impossible. Try reducing the number of exclusions or verify that a valid pairing exists.")
  }

  /**
   * Creates circular pairings from a shuffled list
   * Each person gives to the next person, last person gives to first
   */
  def createCircularPairings(participants: List[String]): List[Pairing] = {
    participants.zipWithIndex.map { case (giver, index) =>
      val receiverIndex = (index + 1) % participants.length
      Pairing(giver, participants(receiverIndex))
    }
  }

  /**
   * Validates that no pairing violates exclusion constraints.
   * Note that exclusions are not checked bidirectionally.
   */
  def isValidPairing(
    pairings: List[Pairing],
    exclusionSet: Set[(String, String)]
  ): Boolean = {
    pairings.forall { pairing =>
      !exclusionSet.contains((pairing.giver, pairing.receiver))
    }
  }
}

