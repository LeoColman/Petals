package br.com.colman.petals.settings

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class AppLanguageTest : FunSpec({
  context("getAppLanguageName") {
    withData(
      nameFn = { (code, name) -> "for code '$code' returns '$name'" },
      "de" to "Deutsch",
      "en" to "English",
      "fr" to "Français",
      "es" to "Español",
      "it" to "Italiano",
      "nl" to "Nederlands",
      "no" to "Norsk",
      "pt" to "Português",
      "ru" to "Русский",
      "tr" to "Türkçe",
      "uk" to "Українськ",
    ) { (code, expectedName) ->
      AppLanguage.getAppLanguageName(code) shouldBe expectedName
    }

    context("edge cases") {
      test("invalid code returns English default") {
        AppLanguage.getAppLanguageName("xx") shouldBe "English"
      }

      test("empty code returns English default") {
        AppLanguage.getAppLanguageName("") shouldBe "English"
      }

      test("case-sensitive - uppercase code returns English") {
        AppLanguage.getAppLanguageName("DE") shouldBe "English"
      }
    }
  }

  context("getAppLanguageCode") {
    withData(
      nameFn = { (name, code) -> "for name '$name' returns '$code'" },
      "Deutsch" to "de",
      "English" to "en",
      "Français" to "fr",
      "Español" to "es",
      "Italiano" to "it",
      "Nederlands" to "nl",
      "Norsk" to "no",
      "Português" to "pt",
      "Русский" to "ru",
      "Türkçe" to "tr",
      "Українськ" to "uk",
    ) { (languageName, expectedCode) ->
      AppLanguage.getAppLanguageCode(languageName) shouldBe expectedCode
    }

    context("edge cases") {
      test("invalid name returns English code") {
        AppLanguage.getAppLanguageCode("Klingon") shouldBe "en"
      }

      test("empty name returns English code") {
        AppLanguage.getAppLanguageCode("") shouldBe "en"
      }

      test("case-sensitive - lowercase name returns English") {
        AppLanguage.getAppLanguageCode("deutsch") shouldBe "en"
      }
    }
  }
})
