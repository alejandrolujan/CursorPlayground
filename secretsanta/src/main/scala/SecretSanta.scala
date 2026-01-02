package secretsanta

/**
 * Main entry point for Secret Santa pairing generator
 */
object SecretSanta {

  /**
   * Main entry point for Secret Santa pairing generator
   * 
   * Program Arguments:
   *   args(0): Input file path (optional)
   *            - Path to the JSON file containing participants and exclusions
   *            - Default: "list.json" if not provided
   *            - Example: "list.json" or "/path/to/config.json"
   * 
   *   args(1): Output file path (optional)
   *            - Path where the generated pairings will be saved
   *            - If not provided, pairings are only printed to console
   *            - Example: "pairings.txt" or "results.json"
   * 
   *   args(2): Output format (optional, only used if args(1) is provided)
   *            - "json" or "JSON": Save output as JSON format
   *            - Any other value or omitted: Save output as readable text format
   *            - Example: "json" for JSON output, "text" or omitted for text output
   * 
   * Usage Examples:
   *   sbt "run"                           # Uses list.json, prints to console only
   *   sbt "run list.json"                 # Explicit input file, prints to console
   *   sbt "run list.json output.txt"      # Saves to text file
   *   sbt "run list.json output.json json" # Saves to JSON file
   */
  def main(args: Array[String]): Unit = {
    // Argument 0: Input file path (defaults to "list.json")
    val inputFile = if (args.length > 0) args(0) else "list.json"
    
    // Argument 1: Optional output file path (None if not provided)
    val outputFile = if (args.length > 1) Some(args(1)) else None
    
    // Argument 2: Output format flag (only used if output file is specified)
    // "json" or "JSON" -> JSON format, anything else -> text format
    val outputJson = if (args.length > 2) args(2).toLowerCase == "json" else false

    // Read configuration from JSON file
    JsonParser.readConfig(inputFile) match {
      case Left(error) =>
        System.err.println(s"Error reading configuration: $error")
        System.exit(1)
      
      case Right(config) =>
        // Generate pairings
        PairingGenerator.generatePairings(config) match {
          case Left(error) =>
            System.err.println(s"Error generating pairings: $error")
            System.exit(1)
          
          case Right(pairings) =>
            // Print pairings to console
            OutputFormatter.printPairings(pairings)
            
            // Optionally save to file
            outputFile.foreach { filePath =>
              val result = if (outputJson) {
                OutputFormatter.savePairingsToJsonFile(pairings, filePath)
              } else {
                OutputFormatter.savePairingsToFile(pairings, filePath)
              }
              
              result match {
                case Left(error) =>
                  System.err.println(s"Warning: Could not save to file: $error")
                case Right(_) =>
                  val format = if (outputJson) "JSON" else "text"
                  println(s"\nPairings saved to $filePath ($format format)")
              }
            }
        }
    }
  }
}

