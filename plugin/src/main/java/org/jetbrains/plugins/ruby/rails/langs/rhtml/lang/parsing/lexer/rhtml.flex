package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;

/* Auto generated File */
@SuppressWarnings({"AccessStaticViaInstance", "FieldCanBeLocal", "UnusedAssignment", "JavaDoc", "UnusedDeclaration", "SimplifiableIfStatement", "ConstantConditions"})
%%

%class _RHTMLFlexLexer
%implements FlexLexer, RHTMLTokenType
%unicode
%public

%function advance
%type IElementType

%eof{ return;
%eof}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// USER CODE //////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
%{
    private int getTagLength(final boolean isOpenTag, final boolean isInComment) {
        final int chPosition = zzMarkedPos - 2;
        final char ch = zzBuffer.charAt(chPosition);

        final char ch1 = zzBuffer.charAt(zzMarkedPos - 1);

        //System.out.println("pos - 1["+ (zzMarkedPos - 1) +"]: [" + ch1 + "]");
        //System.out.println("pos - 2["+ (zzMarkedPos - 2) +"]: [" + ch + "]");

        if (isOpenTag) {
            if (ch == '%') {
                return 3;
            }
            return 0;
        } else {
            if (isInComment) {
                if (ch1 == '\n') {
                    return ch != '\r' ? 1 : 2;
                } else if (ch1 == '\r') {
                    return 1;
                }
            }
            
            final int omitPosition = zzMarkedPos - 3;
            if (ch == '%') {
                if (!isInComment && (omitPosition >= 0 && zzBuffer.charAt(omitPosition) == '-')){
                    return 3;
                } else {
                    if (isInComment && (omitPosition >= 0)) {
                      final char ch3 = zzBuffer.charAt(omitPosition);
                      if (ch3 == '\n' || ch3 == '\r') {
                          return 3;
                      }
                    }
                    return 2;
                }
            }
            return 0;
        }
    }

%}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////// REGEXPS DECLARATIONS //////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
EOL_LEXEM =                            "\r" | "\n" | "\r\n"
ANY_CHAR_LEXEM =                       .|{EOL_LEXEM}

//////////////////////// RHTML //////////////////////////
RHTML_SCRIPTLET_START_LEXEM =          "<%"
RHTML_EXPRESSION_START_LEXEM =         "<%="
RUBY_COMMENT_START_LEXEM =             "<%#"
RHTML_INJECTION_END_LEXEM =            "%>"
OMIT_LINE_MODIFIER_LEXEM =             "-"

/////////////////// GENERAL RHTML //////////////////////
RHTML_INJECTIONS_START_LEXEM =         ({RHTML_SCRIPTLET_START_LEXEM}[^%])|{RHTML_EXPRESSION_START_LEXEM}|{RUBY_COMMENT_START_LEXEM}
RHTML_INJECTIONS_END_LEXEM =           ([^%]{RHTML_INJECTION_END_LEXEM}) | ({OMIT_LINE_MODIFIER_LEXEM}{RHTML_INJECTION_END_LEXEM})

HTML_WITHOUT_RHTML_START_LEXEM =       !({ANY_CHAR_LEXEM}*{RHTML_INJECTIONS_START_LEXEM}{ANY_CHAR_LEXEM}*)
EOF_WITHOUT_RHTML_CLOSE_LEXEM =        !({ANY_CHAR_LEXEM}*{RHTML_INJECTIONS_END_LEXEM}{ANY_CHAR_LEXEM}*)
EOF_WITHOUT_RHTML_CLOSE_AND_EOL_LEXEM =!({ANY_CHAR_LEXEM}*({RHTML_INJECTIONS_END_LEXEM}|{EOL_LEXEM}){ANY_CHAR_LEXEM}*)
COMMENT_WITHOUT_EOL_AND_RHTML_CLOSE_LEXEM =!({ANY_CHAR_LEXEM}*({RHTML_INJECTIONS_END_LEXEM}|{EOL_LEXEM}){ANY_CHAR_LEXEM}*)({RHTML_INJECTIONS_END_LEXEM}|{EOL_LEXEM})


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////// STATES DECLARATIONS //////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
%state RHTML_INJECTION_START_STATE
%state IN_RHTML_SCRIPTLET_STATE, RHTML_SCRIPTLET_END_STATE, IN_OMIT_LINE_MODIFIER_FOR_START_STATE
%state IN_RHTML_EXPRESSION_STATE, RHTML_EXPRESSION_END_STATE
%state IN_RHTML_COMMENT_STATE, IN_RHTML_COMMENT_PROCESS_LINE_STATE, RHTML_COMMENT_END_STATE
%%

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////// RULES declarations ////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

<YYINITIAL>{
{HTML_WITHOUT_RHTML_START_LEXEM}          {
                                            return TEMPLATE_CHARACTERS_IN_RHTML;
                                          }

~{RHTML_INJECTIONS_START_LEXEM}           {
                                             yybegin(RHTML_INJECTION_START_STATE);
                                             yypushback(getTagLength(true, false));
                                             if (yytext().length() != 0) {
                                               return TEMPLATE_CHARACTERS_IN_RHTML;
                                             }
                                          }
}

/////////////////////////////////////////////////////
//////////////////// RHTML Injections  //////////////
/////////////////////////////////////////////////////
<RHTML_INJECTION_START_STATE>{
{RHTML_SCRIPTLET_START_LEXEM}        {  if (zzMarkedPos < zzBuffer.length() && zzBuffer.charAt(zzMarkedPos) == '-'){
                                                 yybegin(IN_OMIT_LINE_MODIFIER_FOR_START_STATE);
                                             } else {
                                                 yybegin(IN_RHTML_SCRIPTLET_STATE);
                                             }
                                             return RHTML_SCRIPTLET_START;
                                          }

{RHTML_EXPRESSION_START_LEXEM}            {  yybegin(IN_RHTML_EXPRESSION_STATE);
                                             return RHTML_EXPRESSION_START;
                                          }
                                          
{RUBY_COMMENT_START_LEXEM}                {  yybegin(IN_RHTML_COMMENT_STATE);
                                             return RHTML_COMMENT_START;
                                          }

}

/// Scriplet
//////////////////
<IN_RHTML_SCRIPTLET_STATE>{
 ~{RHTML_INJECTIONS_END_LEXEM}            {
                                              yypushback(getTagLength(false, false));
                                              yybegin(RHTML_SCRIPTLET_END_STATE);
                                              String text = yytext().toString();
                                              if (text.startsWith( "%>")) {
                                                 yypushback(text.length());                                                 
                                              } else {
                                                 return RUBY_CODE_CHARACTERS;
                                              }
                                          }

{EOF_WITHOUT_RHTML_CLOSE_LEXEM}           {   String text = yytext().toString();
                                              if (text.startsWith( "%>")) {
                                                 yybegin(RHTML_SCRIPTLET_END_STATE);
                                                 yypushback(text.length());
                                              } else {
                                                 return RUBY_CODE_CHARACTERS;
                                              }
                                          }
}

<IN_OMIT_LINE_MODIFIER_FOR_START_STATE>{
{OMIT_LINE_MODIFIER_LEXEM}                {  yybegin(IN_RHTML_SCRIPTLET_STATE);
                                             return OMIT_NEW_LINE;
                                          }
}

<RHTML_SCRIPTLET_END_STATE>{
{RHTML_INJECTION_END_LEXEM}               {  yybegin(YYINITIAL);
                                             return RHTML_SCRIPTLET_END;
                                          }
{OMIT_LINE_MODIFIER_LEXEM}                {  yybegin(RHTML_SCRIPTLET_END_STATE);
                                             return OMIT_NEW_LINE;
                                          }
}


/// Expression
//////////////////
<IN_RHTML_EXPRESSION_STATE>{
 ~{RHTML_INJECTIONS_END_LEXEM}            {   yypushback(getTagLength(false, false));
                                              yybegin(RHTML_EXPRESSION_END_STATE);
                                              if (yytext().length() != 0) {
                                                return RUBY_CODE_CHARACTERS;
                                              }
                                          }

{EOF_WITHOUT_RHTML_CLOSE_LEXEM}           {  return RUBY_CODE_CHARACTERS;
                                          }
}

<RHTML_EXPRESSION_END_STATE>{
{RHTML_INJECTION_END_LEXEM}               {  yybegin(YYINITIAL);
                                             return RHTML_EXPRESSION_END;
                                          }
{OMIT_LINE_MODIFIER_LEXEM}                {  yybegin(RHTML_EXPRESSION_END_STATE);
                                             return OMIT_NEW_LINE;
                                          }
}

/////////////////////////////////////////////////////
//////////////////// RHTML Comment  /////////////////
/////////////////////////////////////////////////////
<IN_RHTML_COMMENT_STATE>{
{COMMENT_WITHOUT_EOL_AND_RHTML_CLOSE_LEXEM} {yypushback(getTagLength(false, true));

                                             yybegin(IN_RHTML_COMMENT_PROCESS_LINE_STATE);
                                             final String currText = yytext().toString();
                                             final int curLen = currText.length();
                                             if (curLen != 0) {
                                               if (currText.trim().length() == 0) {
                                                   return RHTML_COMMENT_WSL;
                                               }
                                               if (!currText.startsWith("%>")) {
                                                   return RHTML_COMMENT_CHARACTERS;
                                               } else {
                                                   yypushback(curLen); 
                                               }
                                             }
                                          }
{EOF_WITHOUT_RHTML_CLOSE_AND_EOL_LEXEM}   {  final String currText = yytext().toString();
                                             if (!currText.startsWith("%>")) {
                                                 return RHTML_COMMENT_CHARACTERS;
                                             } else {
                                                 yybegin(IN_RHTML_COMMENT_PROCESS_LINE_STATE);
                                                 yypushback(currText.length());
                                             }
                                          }
}

<IN_RHTML_COMMENT_PROCESS_LINE_STATE>{
{RHTML_INJECTION_END_LEXEM}               {  yypushback(2);
                                             yybegin(RHTML_COMMENT_END_STATE);
                                          }
{EOL_LEXEM}                               {  yybegin(IN_RHTML_COMMENT_STATE);
                                             return RHTML_COMMENT_EOL;
                                          }
}

<RHTML_COMMENT_END_STATE>{
{RHTML_INJECTION_END_LEXEM}               {  yybegin(YYINITIAL);
                                             return RHTML_COMMENT_END;
                                          }
}

/////////////////////////////////////////////////////
///////  Error fallback /////////////////////////////
/////////////////////////////////////////////////////

.|\n|\t|\r                               { return FLEX_ERROR;
                                         }
