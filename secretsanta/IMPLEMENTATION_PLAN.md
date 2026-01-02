# Secret Santa Pairing Implementation Plan

## Overview
Create a Scala program that reads participants and exclusions from `list.json` and generates a valid Secret Santa pairing where each person gives a gift to exactly one other person, avoiding excluded pairs.

## Components

### 1. Data Model
- **Case classes** for JSON structure:
  - `SecretSantaConfig`: Root case class with `participants: List[String]` and `exclusions: List[(String, String)]`
- **Case class** for pairing result:
  - `Pairing`: `giver: String, receiver: String`

### 2. JSON Parsing
- Use a JSON library (e.g., `circe`, `play-json`, or `uPickle`)
- Parse `list.json` into the data model
- Handle parsing errors gracefully

### 3. Pairing Algorithm
- **Input**: List of participants, List of exclusion pairs
- **Output**: List of valid pairings (giver → receiver)
- **Constraints**:
  - Each person gives to exactly one person
  - Each person receives from exactly one person
  - No person gives to themselves
  - No excluded pairs are matched

### 4. Algorithm Approach
**Option A: Backtracking/Constraint Satisfaction**
- Try random permutations of receivers
- Validate against exclusions
- Retry if invalid pairing found

**Option B: Graph-based**
- Build a graph where nodes are participants
- Edges represent valid pairings (not in exclusions)
- Find a perfect matching (Hamiltonian cycle or similar)

**Option C: Simple Shuffle with Validation**
- Shuffle participants list
- Create circular pairing (person[i] → person[i+1], last → first)
- Check against exclusions
- If invalid, reshuffle and retry (with max attempts)

**Recommended: Option C** (simplest, works well for small groups)

### 5. Edge Case Handling
- **Impossible constraints**: If exclusions make pairing impossible, detect and report
- **Empty participants**: Handle gracefully
- **Odd number of participants**: Still works (circular pairing)
- **All pairs excluded**: Detect impossible scenario

### 6. Output Format
- Print pairings in a readable format
- Optionally save to file (JSON or text)
- Format: "Giver → Receiver" for each pair

### 7. Project Structure
```
src/
  main/
    scala/
      SecretSanta.scala    # Main object with entry point
      models.scala          # Case classes for data model
      pairing.scala         # Pairing algorithm logic
      jsonParser.scala      # JSON parsing utilities
```

### 8. Dependencies (build.sbt)
- Scala version: 2.13.x or 3.x
- JSON library: `circe` or `play-json`
- Optionally: `scala.util.Random` for shuffling

### 9. Implementation Steps
1. Set up Scala project structure (build.sbt, src directories)
2. Add JSON library dependency
3. Create data model case classes
4. Implement JSON parser
5. Implement pairing algorithm with exclusion checking
6. Add main entry point to read file and generate pairings
7. Add error handling and validation
8. Test with the provided list.json

### 10. Example Output
```
Secret Santa Pairings:
Ale → Sebas
Dani → Ari
Sebas → Andre
Ari → Bibi
Andre → Ale
Bibi → Dani
```

## Algorithm Pseudocode (Simple Shuffle Approach)
```
1. Read participants and exclusions from JSON
2. Validate: participants.length >= 2
3. Create exclusion set for fast lookup
4. Attempt pairing generation (max N attempts):
   a. Shuffle participants list
   b. Create circular pairs: (participants[i], participants[(i+1) % n])
   c. Check all pairs against exclusions
   d. If valid, return pairs
   e. If invalid, retry
5. If no valid pairing found after N attempts, report error
6. Print or save results
```

