package secretsanta

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import scala.io.Source
import java.io.File

/**
 * JSON parsing utilities for Secret Santa configuration
 */
object JsonParser {

  /**
   * Custom decoder for ExclusionPair from JSON array of two strings
   */
  implicit val exclusionPairDecoder: Decoder[ExclusionPair] = Decoder.instance { cursor =>
    cursor.as[List[String]].flatMap {
      case List(person1, person2) => Right(ExclusionPair(person1, person2))
      case _ => Left(DecodingFailure("Exclusion pair must contain exactly 2 elements", cursor.history))
    }
  }

  /**
   * Reads and parses the Secret Santa configuration from a JSON file
   * 
   * @param filePath Path to the JSON file
   * @return Either an error message or the parsed SecretSantaConfig
   */
  def readConfig(filePath: String): Either[String, SecretSantaConfig] = {
    try {
      val file = new File(filePath)
      if (!file.exists()) {
        return Left(s"File not found: $filePath")
      }

      val source = Source.fromFile(file)
      val jsonContent = try {
        source.mkString
      } finally {
        source.close()
      }

      parse(jsonContent) match {
        case Left(parsingError) => Left(s"JSON parsing error: ${parsingError.message}")
        case Right(json) =>
          json.as[SecretSantaConfig] match {
            case Left(decodingError) => Left(s"JSON decoding error: ${decodingError.message}")
            case Right(config) => Right(config)
          }
      }
    } catch {
      case e: Exception => Left(s"Error reading file: ${e.getMessage}")
    }
  }
}

