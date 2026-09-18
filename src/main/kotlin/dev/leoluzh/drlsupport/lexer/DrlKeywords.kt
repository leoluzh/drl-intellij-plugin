package dev.leoluzh.drlsupport.lexer

/**
 * Reserved words highlighted as keywords. This is intentionally a flat,
 * non-contextual list: it merges DRL's own structural/rule-attribute
 * keywords with the Java-ish keywords that show up inside `when`/`then`
 * blocks (DRL embeds Java/MVEL expressions there). Real semantic
 * validation (is "not" valid here? is this identifier actually a type?)
 * comes from the Drools Language Server via LSP4IJ, not from this lexer —
 * this list only drives editor coloring.
 */
object DrlKeywords {

    /** Single-token keywords (no hyphen). */
    val SIMPLE: Set<String> = setOf(
        // DRL structure
        "package", "import", "global", "function", "rule", "when", "then", "end",
        "salience", "dialect", "extends", "declare", "query", "enum", "attributes",
        "timer", "calendars", "enabled", "duration",
        // DRL pattern / CE keywords
        "exists", "not", "and", "or", "eval", "from", "collect", "accumulate",
        "over", "init", "action", "reverse", "result", "window", "unit", "in",
        "contains", "memberOf", "matches", "soundslike", "instanceof",
        // literals
        "true", "false", "null", "new", "this", "super",
        // embedded Java/MVEL control flow used in "then" consequences
        "if", "else", "for", "while", "do", "return", "break", "continue",
        "switch", "case", "default", "try", "catch", "finally", "throw", "throws",
        "class", "interface", "public", "private", "protected", "static", "final",
        "void", "int", "long", "float", "double", "boolean", "char", "byte", "short",
        "String",
    )

    /**
     * Keywords written with a hyphen in real DRL source (rule attributes, mostly).
     * The lexer ([DrlLexerAdapter]) scans a plain identifier first and only
     * extends across `-word` segments when the accumulated text matches one
     * of these — otherwise `a-b` stays three separate tokens (`a`, `-`, `b`).
     */
    val HYPHENATED: Set<String> = setOf(
        "no-loop", "lock-on-active", "auto-focus", "agenda-group",
        "activation-group", "ruleflow-group", "date-effective", "date-expires",
        "entry-point",
    )
}
