package org.intellij.markdown.lexer

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownTokenTypes
import java.io.IOException
import java.io.Reader

public class MarkdownLexer(public val originalText: CharSequence,
                           public val bufferStart: Int = 0,
                           public val bufferEnd: Int = originalText.length()) {

    private val baseLexer: _MarkdownLexer

    public var type: IElementType? = null
        private set
    private var nextType: IElementType? = null

    public var tokenStart: Int = 0
        private set
    public var tokenEnd: Int = 0
        private set

    init {
        baseLexer = _MarkdownLexer(null as Reader?)
        baseLexer.reset(originalText, bufferStart, bufferEnd, 0)

        type = advanceBase()
        tokenStart = baseLexer.getTokenStart()

        calcNextType()
    }

    public fun advance(): Boolean {
        return locateToken()
    }

    private fun locateToken(): Boolean {
        `type` = nextType
        tokenStart = tokenEnd
        if (`type` == null) {
            return false
        }

        calcNextType()
        return true
    }

    private fun calcNextType() {
        do {
            tokenEnd = baseLexer.getTokenEnd()
            nextType = advanceBase()
        } while (nextType == `type` && TOKENS_TO_MERGE.contains(`type`))
    }

    private fun advanceBase(): IElementType? {
        try {
            return baseLexer.advance()
        } catch (e: IOException) {
            e.printStackTrace()
            throw AssertionError("This could not be!")
        }

    }

    companion object {
        private val TOKENS_TO_MERGE = setOf(
                MarkdownTokenTypes.TEXT,
                MarkdownTokenTypes.WHITE_SPACE,
                MarkdownTokenTypes.CODE,
                MarkdownTokenTypes.HTML_BLOCK,
                MarkdownTokenTypes.LINK_ID,
                MarkdownTokenTypes.LINK_TITLE,
                MarkdownTokenTypes.URL,
                MarkdownTokenTypes.AUTOLINK,
                MarkdownTokenTypes.EMAIL_AUTOLINK,
                MarkdownTokenTypes.BAD_CHARACTER)
    }
}
