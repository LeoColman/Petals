package br.com.colman.petals.settings

enum class AppLanguage(val languageName: String, val languageCode: String) {
  German("Deutsch", "de"),
  English("English", "en"),
  French("Français", "fr"),
  Spanish("Español", "es"),
  Italian("Italiano", "it"),
  Dutch("Nederlands", "nl"),
  Norwegian("Norsk", "no"),
  Portuguese("Português", "pt"),
  Russian("Русский", "ru"),
  Tamil("தமிழ்", "ta"),
  Turkish("Türkçe", "tr"),
  Ukrainian("Українськ", "uk");

  companion object {
    fun getAppLanguageName(code: String): String {
      return entries.firstOrNull { it.languageCode == code }?.languageName ?: English.languageName
    }

    fun getAppLanguageCode(languageName: String): String {
      return entries.firstOrNull { it.languageName == languageName }?.languageCode ?: English.languageCode
    }
  }
}
