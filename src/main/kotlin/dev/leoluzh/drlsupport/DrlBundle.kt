package dev.leoluzh.drlsupport

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
private const val BUNDLE = "messages.DrlBundle"

/**
 * All user-facing strings produced by this plugin's own Kotlin code go
 * through here. `messages/DrlBundle.properties` (no locale suffix) is the
 * default — and default-*language* — resource file, in English. A locale
 * suffixed variant (`DrlBundle_pt_BR.properties`) is picked up automatically
 * by the standard Java ResourceBundle lookup when the IDE runs under that
 * locale (e.g. with a Brazilian Portuguese language pack installed);
 * otherwise everyone gets the English default. To add another language,
 * drop in another `DrlBundle_<locale>.properties` with the same keys — no
 * code changes needed.
 *
 * Plain XML text in plugin.xml / drl-lsp4ij.xml (plugin name/description,
 * the LSP server's description) is intentionally left as English literals:
 * third-party extension points generally don't resolve resource-bundle
 * keys the way built-in ones (like <action>) do, so routing them through
 * this bundle wouldn't actually get translated at runtime.
 */
object DrlBundle {
    private val INSTANCE = DynamicBundle(DrlBundle::class.java, BUNDLE)

    @Nls
    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String =
        INSTANCE.getMessage(key, *params)
}
