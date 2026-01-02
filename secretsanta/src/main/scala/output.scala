package secretsanta

import java.io.{File, PrintWriter}
import io.circe.syntax._
import io.circe.generic.auto._

/**
 * Output formatting utilities for Secret Santa pairings
 */
object OutputFormatter {

  /**
   * Formats pairings as a readable string
   * 
   * @param pairings List of pairings to format
   * @return Formatted string with "Giver → Receiver" for each pair
   */
  def formatPairings(pairings: List[Pairing]): String = {
    val header = "Secret Santa Pairings:"
    val pairs = pairings.map(p => s"${p.giver} → ${p.receiver}").mkString("\n")
    s"$header\n$pairs"
  }

  /**
   * Formats pairings as JSON
   * 
   * @param pairings List of pairings to format
   * @return JSON string representation
   */
  def formatPairingsAsJson(pairings: List[Pairing]): String = {
    pairings.asJson.spaces2
  }

  /**
   * Prints pairings to console
   * 
   * @param pairings List of pairings to print
   */
  def printPairings(pairings: List[Pairing]): Unit = {
    println(formatPairings(pairings))
  }

  /**
   * Saves pairings to a text file
   * 
   * @param pairings List of pairings to save
   * @param filePath Path to the output file
   * @return Either an error message or success indication
   */
  def savePairingsToFile(pairings: List[Pairing], filePath: String): Either[String, Unit] = {
    try {
      val writer = new PrintWriter(new File(filePath))
      try {
        writer.write(formatPairings(pairings))
        Right(())
      } finally {
        writer.close()
      }
    } catch {
      case e: Exception => Left(s"Error writing to file: ${e.getMessage}")
    }
  }

  /**
   * Saves pairings to a JSON file
   * 
   * @param pairings List of pairings to save
   * @param filePath Path to the output file
   * @return Either an error message or success indication
   */
  def savePairingsToJsonFile(pairings: List[Pairing], filePath: String): Either[String, Unit] = {
    try {
      val writer = new PrintWriter(new File(filePath))
      try {
        writer.write(formatPairingsAsJson(pairings))
        Right(())
      } finally {
        writer.close()
      }
    } catch {
      case e: Exception => Left(s"Error writing to JSON file: ${e.getMessage}")
    }
  }
}

