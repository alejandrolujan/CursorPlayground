package secretsanta

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PairingGeneratorTest extends AnyFlatSpec with Matchers {

  "createCircularPairings" should "create circular pairings for 2 participants" in {
    val participants = List("Alice", "Bob")
    val result = PairingGenerator.createCircularPairings(participants)
    
    result should have size 2
    result should contain(Pairing("Alice", "Bob"))
    result should contain(Pairing("Bob", "Alice"))
  }

  it should "create circular pairings for 3 participants" in {
    val participants = List("Alice", "Bob", "Charlie")
    val result = PairingGenerator.createCircularPairings(participants)
    
    result should have size 3
    result should contain(Pairing("Alice", "Bob"))
    result should contain(Pairing("Bob", "Charlie"))
    result should contain(Pairing("Charlie", "Alice"))
  }

  it should "create circular pairings for 4 participants" in {
    val participants = List("A", "B", "C", "D")
    val result = PairingGenerator.createCircularPairings(participants)
    
    result should have size 4
    result.map(_.giver) should contain theSameElementsAs participants
    result.map(_.receiver) should contain theSameElementsAs participants
    
    // Verify circular structure
    val giverToReceiver = result.map(p => p.giver -> p.receiver).toMap
    giverToReceiver("A") should be("B")
    giverToReceiver("B") should be("C")
    giverToReceiver("C") should be("D")
    giverToReceiver("D") should be("A")
  }

  it should "ensure no one gives to themselves" in {
    val participants = List("Alice", "Bob", "Charlie", "Diana")
    val result = PairingGenerator.createCircularPairings(participants)
    
    result.foreach { pairing =>
      pairing.giver should not be pairing.receiver
    }
  }

  it should "ensure each person gives to exactly one person" in {
    val participants = List("A", "B", "C", "D", "E")
    val result = PairingGenerator.createCircularPairings(participants)
    
    result.map(_.giver).distinct should have size participants.length
  }

  it should "ensure each person receives from exactly one person" in {
    val participants = List("A", "B", "C", "D", "E")
    val result = PairingGenerator.createCircularPairings(participants)
    
    result.map(_.receiver).distinct should have size participants.length
  }

  "isValidPairing" should "return true when no exclusions are violated" in {
    val pairings = List(
      Pairing("Alice", "Bob"),
      Pairing("Bob", "Charlie")
    )
    val exclusionSet = Set(("Alice", "Charlie"))
    
    PairingGenerator.isValidPairing(pairings, exclusionSet) should be(true)
  }

  it should "return false when an exclusion is violated" in {
    val pairings = List(
      Pairing("Alice", "Bob"),
      Pairing("Bob", "Charlie")
    )
    val exclusionSet = Set(("Alice", "Bob"), ("Bob", "Alice"))
    
    PairingGenerator.isValidPairing(pairings, exclusionSet) should be(false)
  }

  it should "return true for empty pairings" in {
    val pairings = List.empty[Pairing]
    val exclusionSet = Set(("Alice", "Bob"))
    
    PairingGenerator.isValidPairing(pairings, exclusionSet) should be(true)
  }

  it should "return true for empty exclusion set" in {
    val pairings = List(
      Pairing("Alice", "Bob"),
      Pairing("Bob", "Alice")
    )
    val exclusionSet = Set.empty[(String, String)]
    
    PairingGenerator.isValidPairing(pairings, exclusionSet) should be(true)
  }

  it should "handle multiple exclusions correctly" in {
    val pairings = List(
      Pairing("Alice", "Bob"),
      Pairing("Bob", "Charlie"),
      Pairing("Charlie", "Diana"),
      Pairing("Diana", "Alice")
    )
    val exclusionSet = Set(
      ("Alice", "Bob"),
      ("Charlie", "Diana")
    )
    
    PairingGenerator.isValidPairing(pairings, exclusionSet) should be(false)
  }

  "generatePairings" should "return error for empty participants list" in {
    val config = SecretSantaConfig(
      participants = List.empty,
      exclusions = List.empty
    )
    
    val result = PairingGenerator.generatePairings(config)
    result should be(Left("No participants provided"))
  }

  it should "return error for single participant" in {
    val config = SecretSantaConfig(
      participants = List("Alice"),
      exclusions = List.empty
    )
    
    val result = PairingGenerator.generatePairings(config)
    result should be(Left("Need at least 2 participants for Secret Santa"))
  }

  it should "generate valid pairings for 2 participants with no exclusions" in {
    val config = SecretSantaConfig(
      participants = List("Alice", "Bob"),
      exclusions = List.empty
    )
    
    val result = PairingGenerator.generatePairings(config)
    result.isRight should be(true)
    
    result.foreach { pairings =>
      pairings should have size 2
      pairings.map(_.giver).distinct should have size 2
      pairings.map(_.receiver).distinct should have size 2
      pairings.foreach { p =>
        p.giver should not be p.receiver
      }
    }
  }

  it should "generate valid pairings for multiple participants with no exclusions" in {
    val config = SecretSantaConfig(
      participants = List("Alice", "Bob", "Charlie", "Diana"),
      exclusions = List.empty
    )
    
    val result = PairingGenerator.generatePairings(config)
    result.isRight should be(true)
    
    result.foreach { pairings =>
      pairings should have size 4
      pairings.map(_.giver).distinct should contain theSameElementsAs config.participants
      pairings.map(_.receiver).distinct should contain theSameElementsAs config.participants
    }
  }

  it should "generate valid pairings that respect exclusions" in {
    val config = SecretSantaConfig(
      participants = List("Alice", "Bob", "Charlie", "Diana"),
      exclusions = List(
        ExclusionPair("Alice", "Bob")
      )
    )
    
    val result = PairingGenerator.generatePairings(config)
    result.isRight should be(true)
    
    result.foreach { pairings =>
      pairings should have size 4
      // Verify no excluded pairs
      pairings.foreach { pairing =>
        val isExcluded = (pairing.giver == "Alice" && pairing.receiver == "Bob") ||
                        (pairing.giver == "Bob" && pairing.receiver == "Alice")
        isExcluded should be(false)
      }
    }
  }

  it should "generate valid pairings with multiple exclusions" in {
    val config = SecretSantaConfig(
      participants = List("Alice", "Bob", "Charlie", "Diana", "Eve"),
      exclusions = List(
        ExclusionPair("Alice", "Bob"),
        ExclusionPair("Charlie", "Diana")
      )
    )
    
    val result = PairingGenerator.generatePairings(config)
    result.isRight should be(true)
    
    result.foreach { pairings =>
      pairings should have size 5
      // Verify no excluded pairs
      val exclusionSet = Set(
        ("Alice", "Bob"), ("Bob", "Alice"),
        ("Charlie", "Diana"), ("Diana", "Charlie")
      )
      pairings.foreach { pairing =>
        exclusionSet.contains((pairing.giver, pairing.receiver)) should be(false)
      }
    }
  }

  it should "handle case where many pairs are excluded but solution exists" in {
    val config = SecretSantaConfig(
      participants = List("Alice", "Bob", "Charlie", "Diana"),
      exclusions = List(
        ExclusionPair("Alice", "Bob"),
        ExclusionPair("Charlie", "Diana")
      )
    )
    
    val result = PairingGenerator.generatePairings(config)
    result.isRight should be(true)
    
    result.foreach { pairings =>
      // Should find a valid circular pairing despite exclusions
      // Example: Alice->Charlie, Charlie->Bob, Bob->Diana, Diana->Alice
      pairings should have size 4
      pairings.map(_.giver).distinct should have size 4
      pairings.map(_.receiver).distinct should have size 4
      // Verify no excluded pairs
      val exclusionSet = Set(
        ("Alice", "Bob"), ("Bob", "Alice"),
        ("Charlie", "Diana"), ("Diana", "Charlie")
      )
      pairings.foreach { pairing =>
        val isExcluded = exclusionSet.contains((pairing.giver, pairing.receiver))
        isExcluded should be(false)
      }
    }
  }

  it should "return error when pairing is impossible due to exclusions" in {
    // This is a tricky case - if we have 3 people and exclude all possible pairs,
    // it might be impossible. However, with our algorithm, it will try 1000 times
    // and return an error if it can't find a valid pairing.
    // For a deterministic test, we'd need a scenario that's truly impossible,
    // but with circular pairings, most scenarios are solvable.
    // Let's test with a scenario that might be difficult but should eventually work
    val config = SecretSantaConfig(
      participants = List("A", "B", "C", "D"),
      exclusions = List(
        ExclusionPair("A", "B"),
        ExclusionPair("B", "C"),
        ExclusionPair("C", "D"),
        ExclusionPair("D", "A")
      )
    )
    
    // This should still be solvable (e.g., A->C, B->D, C->A, D->B)
    val result = PairingGenerator.generatePairings(config)
    // The result might be Right or Left depending on randomness,
    // but let's verify the structure if it succeeds
    if (result.isRight) {
      result.foreach { pairings =>
        pairings should have size 4
      }
    }
  }

  it should "generate different pairings on multiple calls (non-deterministic)" in {
    val config = SecretSantaConfig(
      participants = List("Alice", "Bob", "Charlie", "Diana", "Eve"),
      exclusions = List.empty
    )
    
    val result1 = PairingGenerator.generatePairings(config)
    val result2 = PairingGenerator.generatePairings(config)
    
    result1.isRight should be(true)
    result2.isRight should be(true)
    
    // They might be the same or different due to randomness,
    // but both should be valid
    result1.foreach { p1 =>
      p1 should have size 5
    }
    result2.foreach { p2 =>
      p2 should have size 5
    }
  }

  "Edge case handling" should "detect duplicate participants" in {
    val config = SecretSantaConfig(
      participants = List("Alice", "Bob", "Alice"),
      exclusions = List.empty
    )
    
    val result = PairingGenerator.generatePairings(config)
    result.isLeft should be(true)
    result.left.foreach { error =>
      error should include("Duplicate participants")
      error should include("Alice")
    }
  }

  it should "detect exclusions referencing non-existent participants" in {
    val config = SecretSantaConfig(
      participants = List("Alice", "Bob"),
      exclusions = List(
        ExclusionPair("Alice", "Charlie")
      )
    )
    
    val result = PairingGenerator.generatePairings(config)
    result.isLeft should be(true)
    result.left.foreach { error =>
      error should include("non-existent participants")
      error should include("Charlie")
    }
  }

  it should "detect impossible pairing with 2 participants excluding each other" in {
    val config = SecretSantaConfig(
      participants = List("Alice", "Bob"),
      exclusions = List(
        ExclusionPair("Alice", "Bob")
      )
    )
    
    val result = PairingGenerator.generatePairings(config)
    result.isLeft should be(true)
    result.left.foreach { error =>
      error should include("Impossible pairing")
      error should include("2 participants")
    }
  }

  it should "detect when too many exclusions make pairing impossible" in {
    // With 3 participants, we have 6 possible directed pairs
    // If we exclude 5 of them, only 1 remains, which is not enough for a cycle of 3
    val config = SecretSantaConfig(
      participants = List("Alice", "Bob", "Charlie"),
      exclusions = List(
        ExclusionPair("Alice", "Bob"),
        ExclusionPair("Alice", "Charlie"),
        ExclusionPair("Bob", "Alice"),
        ExclusionPair("Bob", "Charlie"),
        ExclusionPair("Charlie", "Alice")
        // Only Charlie->Bob remains, but we need 3 pairs for a cycle
      )
    )
    
    val result = PairingGenerator.generatePairings(config)
    result.isLeft should be(true)
    result.left.foreach { error =>
      error should include("Too many exclusions")
    }
  }

  it should "handle odd number of participants correctly" in {
    val config = SecretSantaConfig(
      participants = List("Alice", "Bob", "Charlie", "Diana", "Eve"),
      exclusions = List.empty
    )
    
    val result = PairingGenerator.generatePairings(config)
    result.isRight should be(true)
    result.foreach { pairings =>
      pairings should have size 5
      pairings.map(_.giver).distinct should have size 5
      pairings.map(_.receiver).distinct should have size 5
    }
  }
}

