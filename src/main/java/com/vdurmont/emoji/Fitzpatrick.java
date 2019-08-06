package com.vdurmont.emoji;

/**
 * Enum that represents the Fitzpatrick modifiers supported by the emojis.
 */
public enum Fitzpatrick {
  /**
   * Fitzpatrick modifier of type 1/2 (pale white/white)
   */
  TYPE_1_2("\uD83C\uDFFB", "&#127995;", "&#x1F3FB;"),

  /**
   * Fitzpatrick modifier of type 3 (cream white)
   */
  TYPE_3("\uD83C\uDFFC","&#127996;", "&#x1F3FC;"),

  /**
   * Fitzpatrick modifier of type 4 (moderate brown)
   */
  TYPE_4("\uD83C\uDFFD","&#127997;", "&#x1F3FD;"),

  /**
   * Fitzpatrick modifier of type 5 (dark brown)
   */
  TYPE_5("\uD83C\uDFFE","&#127998;", "&#x1F3FE;"),

  /**
   * Fitzpatrick modifier of type 6 (black)
   */
  TYPE_6("\uD83C\uDFFF","&#127999;", "&#x1F3FF;");

  /**
   * The unicode representation of the Fitzpatrick modifier
   */
  public final String unicode;

  public final String htmlDecimal;

  public final String htmlHexaDecimal;

  Fitzpatrick(String unicode, String htmlDecimal, String htmlHexaDecimal) {
    this.unicode = unicode;
    this.htmlDecimal = htmlDecimal;
    this.htmlHexaDecimal = htmlHexaDecimal;
  }


  public static Fitzpatrick fitzpatrickFromUnicode(String unicode) {
    for (Fitzpatrick v : values()) {
      if (v.unicode.equals(unicode)) {
        return v;
      }
    }
    return null;
  }

  public static Fitzpatrick fitzpatrickFromType(String type) {
    try {
      return Fitzpatrick.valueOf(type.toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
