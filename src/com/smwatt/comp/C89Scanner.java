/* The following code was generated by JFlex 1.4.3 on 11/30/10 2:13 PM */

//////////////////////////////////////////////////////////////////////////////
//
// C89Scanner.lex
//
//////////////////////////////////////////////////////////////////////////////
// Copyright 2005 Stephen M. Watt

package com.smwatt.comp;
import java_cup.runtime.*;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 11/30/10 2:13 PM from the specification file
 * <tt>/Users/petergoodman/Code/CCompiler2/src/com/smwatt/comp/C89Scanner.lex</tt>
 */
public class C89Scanner implements CScanner, java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0, 0
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\16\1\46\1\0\2\16\22\0\1\16\1\61\1\44\1\76"+
    "\1\0\1\63\1\65\1\47\1\53\1\54\1\15\1\67\1\72\1\5"+
    "\1\52\1\14\1\50\11\1\1\74\1\73\1\70\1\62\1\71\1\75"+
    "\1\0\4\3\1\4\1\7\5\2\1\11\10\2\1\13\2\2\1\51"+
    "\2\2\1\55\1\45\1\56\1\64\1\2\1\0\1\20\1\23\1\27"+
    "\1\34\1\25\1\6\1\37\1\31\1\33\1\2\1\26\1\10\1\35"+
    "\1\32\1\22\1\43\1\2\1\24\1\30\1\21\1\12\1\17\1\41"+
    "\1\36\1\42\1\40\1\57\1\66\1\60\1\77\uff81\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\1\2\1\3\1\4\3\3\1\5\1\6"+
    "\2\7\13\3\2\1\1\2\1\10\1\11\1\12\1\13"+
    "\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23"+
    "\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\33"+
    "\1\34\1\35\1\0\1\2\1\36\1\37\1\40\1\41"+
    "\4\3\1\0\1\42\1\43\17\3\1\44\1\3\1\45"+
    "\3\3\1\0\1\46\5\0\1\47\1\50\1\51\1\52"+
    "\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62"+
    "\1\63\1\64\1\65\1\36\1\0\1\36\1\3\1\66"+
    "\3\3\1\0\23\3\1\67\4\3\1\70\1\2\1\71"+
    "\1\72\1\73\1\3\1\74\3\3\1\75\1\76\4\3"+
    "\1\77\1\100\1\3\1\101\2\3\1\102\10\3\1\103"+
    "\1\3\1\104\1\3\1\105\2\3\1\106\4\3\1\107"+
    "\2\3\1\110\5\3\1\111\3\3\1\112\1\3\1\113"+
    "\1\3\1\114\1\115\1\116\1\117\1\120\1\121\3\3"+
    "\1\122\2\3\1\123\1\124\1\125\1\126\1\127";

  private static int [] zzUnpackAction() {
    int [] result = new int[214];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\100\0\200\0\300\0\u0100\0\u0140\0\u0180\0\u01c0"+
    "\0\u0200\0\u0240\0\100\0\u0280\0\u02c0\0\u0300\0\u0340\0\u0380"+
    "\0\u03c0\0\u0400\0\u0440\0\u0480\0\u04c0\0\u0500\0\u0540\0\u0580"+
    "\0\u05c0\0\u0600\0\u0640\0\100\0\100\0\100\0\100\0\100"+
    "\0\100\0\u0680\0\u06c0\0\u0700\0\u0740\0\u0780\0\u07c0\0\u0800"+
    "\0\u0840\0\u0880\0\100\0\100\0\100\0\100\0\u08c0\0\100"+
    "\0\u0900\0\u0940\0\u0980\0\100\0\100\0\100\0\u09c0\0\u0a00"+
    "\0\u0a40\0\u0a80\0\u0ac0\0\100\0\100\0\u0b00\0\u0b40\0\u0b80"+
    "\0\u0bc0\0\u0c00\0\u0c40\0\u0c80\0\u0cc0\0\u0d00\0\u0d40\0\u0d80"+
    "\0\u0dc0\0\u0e00\0\u0e40\0\u0e80\0\300\0\u0ec0\0\u0f00\0\u0f40"+
    "\0\u0f80\0\u0fc0\0\u0580\0\100\0\u1000\0\u1040\0\u1080\0\u10c0"+
    "\0\u1100\0\100\0\100\0\100\0\100\0\100\0\100\0\100"+
    "\0\100\0\100\0\100\0\100\0\u1140\0\100\0\u1180\0\100"+
    "\0\u11c0\0\u1200\0\100\0\u1240\0\300\0\u1280\0\u12c0\0\u1300"+
    "\0\u1340\0\u1380\0\u13c0\0\u1400\0\u1440\0\u1480\0\u14c0\0\u1500"+
    "\0\u1540\0\u1580\0\u15c0\0\u1600\0\u1640\0\u1680\0\u16c0\0\u1700"+
    "\0\u1740\0\u1780\0\u17c0\0\u1800\0\300\0\u1840\0\u1880\0\u18c0"+
    "\0\u1900\0\100\0\u1940\0\100\0\100\0\100\0\u1980\0\300"+
    "\0\u19c0\0\u1a00\0\u1a40\0\300\0\300\0\u1a80\0\u1ac0\0\u1b00"+
    "\0\u1b40\0\300\0\300\0\u1b80\0\300\0\u1bc0\0\u1c00\0\300"+
    "\0\u1c40\0\u1c80\0\u1cc0\0\u1d00\0\u1d40\0\u1d80\0\u1dc0\0\u1e00"+
    "\0\300\0\u1e40\0\300\0\u1e80\0\300\0\u1ec0\0\u1f00\0\300"+
    "\0\u1f40\0\u1f80\0\u1fc0\0\u2000\0\300\0\u2040\0\u2080\0\300"+
    "\0\u20c0\0\u2100\0\u2140\0\u2180\0\u21c0\0\300\0\u2200\0\u2240"+
    "\0\u2280\0\300\0\u22c0\0\300\0\u2300\0\300\0\300\0\300"+
    "\0\300\0\300\0\300\0\u2340\0\u2380\0\u23c0\0\300\0\u2400"+
    "\0\u2440\0\300\0\300\0\300\0\300\0\300";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[214];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\1\3\3\4\1\5\1\6\1\4\1\7\1\4"+
    "\1\10\1\4\1\11\1\12\1\13\1\14\1\15\1\16"+
    "\1\4\1\17\1\20\1\21\1\4\1\22\1\23\2\4"+
    "\1\24\1\25\2\4\1\26\1\4\1\27\2\4\1\30"+
    "\1\2\1\13\1\31\1\32\1\4\1\33\1\34\1\35"+
    "\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\45"+
    "\1\46\1\47\1\50\1\51\1\52\1\53\1\54\1\55"+
    "\1\56\1\57\1\60\101\0\1\3\2\0\1\61\3\0"+
    "\4\62\11\0\1\61\22\0\1\3\1\0\1\63\26\0"+
    "\4\4\1\0\6\4\3\0\25\4\4\0\2\4\33\0"+
    "\1\64\54\0\1\65\6\0\1\66\7\0\4\4\1\0"+
    "\2\4\1\67\3\4\3\0\3\4\1\70\21\4\4\0"+
    "\2\4\27\0\4\4\1\0\6\4\3\0\3\4\1\71"+
    "\21\4\4\0\2\4\27\0\4\4\1\0\6\4\3\0"+
    "\13\4\1\72\11\4\4\0\2\4\43\0\1\73\44\0"+
    "\1\74\77\0\1\75\16\0\4\4\1\0\6\4\3\0"+
    "\3\4\1\76\21\4\4\0\2\4\27\0\4\4\1\0"+
    "\4\4\1\77\1\4\3\0\25\4\4\0\2\4\27\0"+
    "\4\4\1\0\6\4\3\0\23\4\1\100\1\4\4\0"+
    "\2\4\27\0\4\4\1\0\6\4\3\0\5\4\1\101"+
    "\17\4\4\0\2\4\27\0\4\4\1\0\6\4\3\0"+
    "\6\4\1\102\16\4\4\0\2\4\27\0\4\4\1\0"+
    "\2\4\1\103\3\4\3\0\13\4\1\104\3\4\1\105"+
    "\5\4\4\0\2\4\27\0\4\4\1\0\6\4\3\0"+
    "\1\4\1\106\1\4\1\107\6\4\1\110\12\4\4\0"+
    "\2\4\27\0\4\4\1\0\6\4\3\0\2\4\1\111"+
    "\7\4\1\112\1\4\1\113\5\4\1\114\2\4\4\0"+
    "\2\4\27\0\4\4\1\0\1\115\5\4\3\0\13\4"+
    "\1\116\11\4\4\0\2\4\27\0\4\4\1\0\6\4"+
    "\3\0\3\4\1\117\2\4\1\120\16\4\4\0\2\4"+
    "\27\0\4\4\1\0\6\4\3\0\3\4\1\121\21\4"+
    "\4\0\2\4\27\0\4\4\1\0\6\4\3\0\12\4"+
    "\1\122\12\4\4\0\2\4\26\0\44\123\1\124\1\125"+
    "\32\123\45\126\1\127\1\126\1\0\30\126\1\0\1\3"+
    "\2\0\1\61\3\0\4\62\11\0\1\61\10\0\1\130"+
    "\11\0\1\3\1\130\1\63\26\0\1\63\46\0\1\63"+
    "\1\0\1\131\107\0\1\132\77\0\1\133\77\0\1\134"+
    "\77\0\1\135\77\0\1\136\2\0\1\137\74\0\1\140"+
    "\3\0\1\141\73\0\1\142\4\0\1\143\72\0\1\144"+
    "\5\0\1\145\71\0\1\146\6\0\1\147\104\0\1\150"+
    "\2\0\1\151\3\0\1\152\42\0\1\151\16\0\1\152"+
    "\20\0\4\62\65\0\1\63\2\0\1\61\1\0\4\153"+
    "\13\0\1\61\22\0\1\63\30\0\4\4\1\0\6\4"+
    "\3\0\3\4\1\154\21\4\4\0\2\4\27\0\4\4"+
    "\1\0\6\4\3\0\5\4\1\155\17\4\4\0\2\4"+
    "\27\0\4\4\1\0\6\4\3\0\13\4\1\156\11\4"+
    "\4\0\2\4\27\0\4\4\1\0\6\4\3\0\11\4"+
    "\1\157\2\4\1\160\10\4\4\0\2\4\26\0\15\73"+
    "\1\161\62\73\1\0\4\4\1\0\2\4\1\162\3\4"+
    "\3\0\14\4\1\163\10\4\4\0\2\4\27\0\4\4"+
    "\1\0\6\4\3\0\2\4\1\164\22\4\4\0\2\4"+
    "\27\0\4\4\1\0\6\4\3\0\24\4\1\165\4\0"+
    "\2\4\27\0\4\4\1\0\6\4\3\0\6\4\1\166"+
    "\16\4\4\0\2\4\27\0\4\4\1\0\6\4\3\0"+
    "\2\4\1\167\15\4\1\170\4\4\4\0\2\4\27\0"+
    "\4\4\1\0\6\4\3\0\11\4\1\171\13\4\4\0"+
    "\2\4\27\0\4\4\1\0\4\4\1\172\1\4\3\0"+
    "\25\4\4\0\2\4\27\0\4\4\1\0\6\4\3\0"+
    "\2\4\1\173\22\4\4\0\2\4\27\0\4\4\1\0"+
    "\6\4\3\0\11\4\1\174\13\4\4\0\2\4\27\0"+
    "\4\4\1\0\6\4\3\0\13\4\1\175\11\4\4\0"+
    "\2\4\27\0\4\4\1\0\6\4\3\0\1\4\1\176"+
    "\23\4\4\0\2\4\27\0\4\4\1\0\6\4\3\0"+
    "\1\4\1\177\3\4\1\200\17\4\4\0\2\4\27\0"+
    "\4\4\1\0\6\4\3\0\3\4\1\201\21\4\4\0"+
    "\2\4\27\0\4\4\1\0\6\4\3\0\20\4\1\202"+
    "\1\203\3\4\4\0\2\4\27\0\4\4\1\0\6\4"+
    "\3\0\14\4\1\204\10\4\4\0\2\4\27\0\4\4"+
    "\1\0\6\4\3\0\2\4\1\205\22\4\4\0\2\4"+
    "\27\0\4\4\1\0\4\4\1\206\1\4\3\0\25\4"+
    "\4\0\2\4\27\0\4\4\1\0\1\207\5\4\3\0"+
    "\25\4\4\0\2\4\27\0\4\4\1\0\6\4\3\0"+
    "\2\4\1\210\22\4\4\0\2\4\27\0\4\4\1\0"+
    "\6\4\3\0\14\4\1\211\10\4\4\0\2\4\26\0"+
    "\46\123\1\0\31\123\45\126\1\127\1\126\1\212\76\126"+
    "\1\0\31\126\1\0\1\213\1\0\2\213\1\0\2\213"+
    "\10\0\1\213\2\0\1\213\1\0\1\213\1\0\1\213"+
    "\4\0\1\213\13\0\1\213\101\0\1\214\107\0\1\215"+
    "\77\0\1\216\16\0\1\151\4\0\4\153\36\0\1\151"+
    "\30\0\1\151\46\0\1\151\30\0\4\4\1\0\6\4"+
    "\3\0\1\4\1\217\23\4\4\0\2\4\27\0\4\4"+
    "\1\0\6\4\3\0\20\4\1\220\4\4\4\0\2\4"+
    "\27\0\4\4\1\0\6\4\3\0\14\4\1\221\10\4"+
    "\4\0\2\4\27\0\4\4\1\0\6\4\3\0\3\4"+
    "\1\222\21\4\4\0\2\4\26\0\14\73\1\13\1\161"+
    "\62\73\1\0\4\4\1\0\6\4\3\0\1\4\1\223"+
    "\23\4\4\0\2\4\27\0\4\4\1\0\6\4\3\0"+
    "\15\4\1\224\7\4\4\0\2\4\27\0\4\4\1\0"+
    "\6\4\3\0\3\4\1\225\21\4\4\0\2\4\27\0"+
    "\4\4\1\0\6\4\3\0\6\4\1\226\16\4\4\0"+
    "\2\4\27\0\4\4\1\0\6\4\3\0\1\4\1\227"+
    "\23\4\4\0\2\4\27\0\4\4\1\0\4\4\1\230"+
    "\1\4\3\0\25\4\4\0\2\4\27\0\4\4\1\0"+
    "\6\4\3\0\14\4\1\231\10\4\4\0\2\4\27\0"+
    "\4\4\1\0\6\4\3\0\6\4\1\232\16\4\4\0"+
    "\2\4\27\0\4\4\1\0\6\4\3\0\16\4\1\233"+
    "\6\4\4\0\2\4\27\0\4\4\1\0\6\4\3\0"+
    "\6\4\1\234\16\4\4\0\2\4\27\0\4\4\1\0"+
    "\6\4\3\0\6\4\1\235\16\4\4\0\2\4\27\0"+
    "\4\4\1\0\6\4\3\0\2\4\1\236\6\4\1\237"+
    "\13\4\4\0\2\4\27\0\4\4\1\0\6\4\3\0"+
    "\5\4\1\240\17\4\4\0\2\4\27\0\4\4\1\0"+
    "\6\4\3\0\2\4\1\241\22\4\4\0\2\4\27\0"+
    "\4\4\1\0\4\4\1\242\1\4\3\0\25\4\4\0"+
    "\2\4\27\0\4\4\1\0\6\4\3\0\5\4\1\243"+
    "\17\4\4\0\2\4\27\0\4\4\1\0\6\4\3\0"+
    "\13\4\1\244\11\4\4\0\2\4\27\0\4\4\1\0"+
    "\6\4\3\0\6\4\1\245\16\4\4\0\2\4\27\0"+
    "\4\4\1\0\6\4\3\0\2\4\1\246\22\4\4\0"+
    "\2\4\27\0\4\4\1\0\6\4\3\0\4\4\1\247"+
    "\20\4\4\0\2\4\27\0\4\4\1\0\6\4\3\0"+
    "\1\4\1\250\23\4\4\0\2\4\27\0\4\4\1\0"+
    "\6\4\3\0\3\4\1\251\21\4\4\0\2\4\27\0"+
    "\4\4\1\0\2\4\1\252\3\4\3\0\25\4\4\0"+
    "\2\4\27\0\1\213\1\0\2\213\1\0\2\213\4\62"+
    "\4\0\1\213\2\0\1\213\1\0\1\213\1\0\1\213"+
    "\4\0\1\213\13\0\1\213\30\0\4\4\1\0\6\4"+
    "\3\0\2\4\1\253\22\4\4\0\2\4\27\0\4\4"+
    "\1\0\6\4\3\0\20\4\1\254\4\4\4\0\2\4"+
    "\27\0\4\4\1\0\6\4\3\0\13\4\1\255\11\4"+
    "\4\0\2\4\27\0\4\4\1\0\6\4\3\0\2\4"+
    "\1\256\22\4\4\0\2\4\27\0\4\4\1\0\6\4"+
    "\3\0\15\4\1\257\7\4\4\0\2\4\27\0\4\4"+
    "\1\0\6\4\3\0\7\4\1\260\15\4\4\0\2\4"+
    "\27\0\4\4\1\0\6\4\3\0\5\4\1\261\17\4"+
    "\4\0\2\4\27\0\4\4\1\0\6\4\3\0\11\4"+
    "\1\262\13\4\4\0\2\4\27\0\4\4\1\0\6\4"+
    "\3\0\5\4\1\263\17\4\4\0\2\4\27\0\4\4"+
    "\1\0\6\4\3\0\14\4\1\264\10\4\4\0\2\4"+
    "\27\0\4\4\1\0\6\4\3\0\2\4\1\265\22\4"+
    "\4\0\2\4\27\0\4\4\1\0\6\4\3\0\14\4"+
    "\1\266\10\4\4\0\2\4\27\0\4\4\1\0\6\4"+
    "\3\0\10\4\1\267\14\4\4\0\2\4\27\0\4\4"+
    "\1\0\6\4\3\0\2\4\1\270\22\4\4\0\2\4"+
    "\27\0\4\4\1\0\6\4\3\0\6\4\1\271\16\4"+
    "\4\0\2\4\27\0\4\4\1\0\6\4\3\0\3\4"+
    "\1\272\21\4\4\0\2\4\27\0\4\4\1\0\6\4"+
    "\3\0\10\4\1\273\14\4\4\0\2\4\27\0\4\4"+
    "\1\0\2\4\1\274\3\4\3\0\25\4\4\0\2\4"+
    "\27\0\4\4\1\0\4\4\1\275\1\4\3\0\25\4"+
    "\4\0\2\4\27\0\4\4\1\0\6\4\3\0\6\4"+
    "\1\276\16\4\4\0\2\4\27\0\4\4\1\0\6\4"+
    "\3\0\13\4\1\277\11\4\4\0\2\4\27\0\4\4"+
    "\1\0\6\4\3\0\14\4\1\300\10\4\4\0\2\4"+
    "\27\0\4\4\1\0\6\4\3\0\6\4\1\301\16\4"+
    "\4\0\2\4\27\0\4\4\1\0\6\4\3\0\13\4"+
    "\1\302\11\4\4\0\2\4\27\0\4\4\1\0\6\4"+
    "\3\0\2\4\1\303\22\4\4\0\2\4\27\0\4\4"+
    "\1\0\6\4\3\0\13\4\1\304\11\4\4\0\2\4"+
    "\27\0\4\4\1\0\6\4\3\0\13\4\1\305\11\4"+
    "\4\0\2\4\27\0\4\4\1\0\6\4\3\0\10\4"+
    "\1\306\14\4\4\0\2\4\27\0\4\4\1\0\6\4"+
    "\3\0\2\4\1\307\22\4\4\0\2\4\27\0\4\4"+
    "\1\0\6\4\3\0\15\4\1\310\7\4\4\0\2\4"+
    "\27\0\4\4\1\0\1\311\5\4\3\0\25\4\4\0"+
    "\2\4\27\0\4\4\1\0\6\4\3\0\12\4\1\312"+
    "\12\4\4\0\2\4\27\0\4\4\1\0\6\4\3\0"+
    "\6\4\1\313\16\4\4\0\2\4\27\0\4\4\1\0"+
    "\2\4\1\314\3\4\3\0\25\4\4\0\2\4\27\0"+
    "\4\4\1\0\6\4\3\0\6\4\1\315\16\4\4\0"+
    "\2\4\27\0\4\4\1\0\2\4\1\316\3\4\3\0"+
    "\25\4\4\0\2\4\27\0\4\4\1\0\1\317\5\4"+
    "\3\0\25\4\4\0\2\4\27\0\4\4\1\0\6\4"+
    "\3\0\6\4\1\320\16\4\4\0\2\4\27\0\4\4"+
    "\1\0\4\4\1\321\1\4\3\0\25\4\4\0\2\4"+
    "\27\0\4\4\1\0\6\4\3\0\2\4\1\322\22\4"+
    "\4\0\2\4\27\0\4\4\1\0\6\4\3\0\15\4"+
    "\1\323\7\4\4\0\2\4\27\0\4\4\1\0\6\4"+
    "\3\0\6\4\1\324\16\4\4\0\2\4\27\0\4\4"+
    "\1\0\6\4\3\0\5\4\1\325\17\4\4\0\2\4"+
    "\27\0\4\4\1\0\6\4\3\0\6\4\1\326\16\4"+
    "\4\0\2\4\26\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[9344];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\10\1\1\11\20\1\6\11\11\1\4\11"+
    "\1\1\1\11\1\0\2\1\3\11\4\1\1\0\2\11"+
    "\25\1\1\0\1\11\5\0\13\11\1\1\1\11\1\1"+
    "\1\11\1\1\1\0\1\11\5\1\1\0\30\1\1\11"+
    "\1\1\3\11\110\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[214];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */

  //-- The caller can tell us the filename.
  private String _fname = "-- Unknown File --";
  public void setFilename(String s) {
    _fname = s;
  }
  public String getFilename() {
    return _fname;
  }
  //-- The parser sets a "CTypedefOracle"
  private CTypedefOracle _tdoracle = null;
  
  public void setTypedefOracle(CTypedefOracle tdoracle) { 
  	_tdoracle = tdoracle; 
  }
  public CTypedefOracle getTypedefOracle() {
  	return _tdoracle;
  }
  private boolean isTypedefName(String s) {
  	return _tdoracle != null && _tdoracle.isTypedefName(s);
  }
  
  //-- These are used to make the symbols for the parser.
  private Symbol token(int type) {
    return new Symbol(type, yyline, yycolumn, new CToken(type, _fname, yyline, yycolumn));
  }
  private Symbol string(int type, String value) {
    return new Symbol(type, yyline, yycolumn, new CToken(type, _fname, yyline, yycolumn, value));
  }
  private Symbol specifier(int type) {
    return new Symbol(type, yyline, yycolumn, new CTokenSpecifier(type, _fname, yyline, yycolumn));
  }
  private Symbol operator(int type) {
    return new Symbol(type, yyline, yycolumn, new CTokenOperator(type, _fname, yyline, yycolumn));
  }



  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public C89Scanner(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public C89Scanner(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 156) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                                                             zzCurrentPosL++) {
        switch (zzBufferL[zzCurrentPosL]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn++;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 72: 
          { return specifier(CTokenType.SHORT);
          }
        case 88: break;
        case 28: 
          { return token    (CTokenType.POUND);
          }
        case 89: break;
        case 62: 
          { return specifier    (CTokenType.AUTO);
          }
        case 90: break;
        case 15: 
          { return operator (CTokenType.NOT);
          }
        case 91: break;
        case 64: 
          { return token    (CTokenType.ENUM);
          }
        case 92: break;
        case 54: 
          { return token    (CTokenType.FOR);
          }
        case 93: break;
        case 31: 
          { return operator (CTokenType.MINUS_MINUS);
          }
        case 94: break;
        case 71: 
          { return specifier(CTokenType.CONST);
          }
        case 95: break;
        case 67: 
          { return token    (CTokenType.GOTO);
          }
        case 96: break;
        case 13: 
          { return token    (CTokenType.O_BRACE);
          }
        case 97: break;
        case 58: 
          { return operator (CTokenType.LSH_ASSIGN);
          }
        case 98: break;
        case 57: 
          { return token    (CTokenType.DOT_DOT_DOT);
          }
        case 99: break;
        case 66: 
          { return specifier(CTokenType.CHAR);
          }
        case 100: break;
        case 23: 
          { return operator (CTokenType.GT);
          }
        case 101: break;
        case 27: 
          { return operator    (CTokenType.QMARK);
          }
        case 102: break;
        case 45: 
          { return operator (CTokenType.VBAR_ASSIGN);
          }
        case 103: break;
        case 35: 
          { return operator (CTokenType.STAR_ASSIGN);
          }
        case 104: break;
        case 18: 
          { return operator (CTokenType.XOR);
          }
        case 105: break;
        case 2: 
          { return string   (CTokenType.INTEGER_CONSTANT, yytext());
          }
        case 106: break;
        case 12: 
          { return token    (CTokenType.C_BRACK);
          }
        case 107: break;
        case 34: 
          { return operator (CTokenType.SLASH_ASSIGN);
          }
        case 108: break;
        case 80: 
          { return token    (CTokenType.SWITCH);
          }
        case 109: break;
        case 44: 
          { return operator (CTokenType.AMP_AMP);
          }
        case 110: break;
        case 48: 
          { return operator (CTokenType.PLUS_PLUS);
          }
        case 111: break;
        case 22: 
          { return operator (CTokenType.LT);
          }
        case 112: break;
        case 36: 
          { return token    (CTokenType.IF);
          }
        case 113: break;
        case 86: 
          { return specifier(CTokenType.REGISTER);
          }
        case 114: break;
        case 78: 
          { return specifier(CTokenType.SIGNED);
          }
        case 115: break;
        case 40: 
          { return operator (CTokenType.EQUALS);
          }
        case 116: break;
        case 76: 
          { return specifier(CTokenType.STATIC);
          }
        case 117: break;
        case 16: 
          { return operator (CTokenType.ASSIGN);
          }
        case 118: break;
        case 61: 
          { return specifier(CTokenType.VOID);
          }
        case 119: break;
        case 65: 
          { return token    (CTokenType.CASE);
          }
        case 120: break;
        case 56: 
          { return string   (CTokenType.CHARACTER_CONSTANT, yytext());
          }
        case 121: break;
        case 21: 
          { return operator (CTokenType.PLUS);
          }
        case 122: break;
        case 43: 
          { return operator (CTokenType.AMP_ASSIGN);
          }
        case 123: break;
        case 68: 
          { return specifier(CTokenType.FLOAT);
          }
        case 124: break;
        case 70: 
          { return token    (CTokenType.BREAK);
          }
        case 125: break;
        case 39: 
          { return operator (CTokenType.NOT_EQUALS);
          }
        case 126: break;
        case 47: 
          { return operator (CTokenType.PLUS_ASSIGN);
          }
        case 127: break;
        case 14: 
          { return token    (CTokenType.C_BRACE);
          }
        case 128: break;
        case 83: 
          { return token    (CTokenType.DEFAULT);
          }
        case 129: break;
        case 84: 
          { return specifier(CTokenType.UNSIGNED);
          }
        case 130: break;
        case 53: 
          { return token    (CTokenType.POUND_POUND);
          }
        case 131: break;
        case 55: 
          { return specifier(CTokenType.INT);
          }
        case 132: break;
        case 87: 
          { return token    (CTokenType.CONTINUE);
          }
        case 133: break;
        case 30: 
          { return string   (CTokenType.FLOATING_CONSTANT, yytext());
          }
        case 134: break;
        case 19: 
          { return operator (CTokenType.AMP);
          }
        case 135: break;
        case 4: 
          { return operator (CTokenType.MINUS);
          }
        case 136: break;
        case 9: 
          { return token    (CTokenType.O_PAREN);
          }
        case 137: break;
        case 59: 
          { return operator (CTokenType.RSH_ASSIGN);
          }
        case 138: break;
        case 63: 
          { return token    (CTokenType.ELSE);
          }
        case 139: break;
        case 5: 
          { return operator (CTokenType.SLASH);
          }
        case 140: break;
        case 29: 
          { return operator (CTokenType.TILDE);
          }
        case 141: break;
        case 26: 
          { return token    (CTokenType.COLON);
          }
        case 142: break;
        case 79: 
          { return token    (CTokenType.SIZEOF);
          }
        case 143: break;
        case 85: 
          { return specifier(CTokenType.VOLATILE);
          }
        case 144: break;
        case 42: 
          { return operator (CTokenType.XOR_ASSIGN);
          }
        case 145: break;
        case 17: 
          { return operator (CTokenType.MOD);
          }
        case 146: break;
        case 82: 
          { return specifier(CTokenType.TYPEDEF);
          }
        case 147: break;
        case 51: 
          { return operator (CTokenType.GT_EQ);
          }
        case 148: break;
        case 1: 
          { return string   (CTokenType.ERROR, yytext());
          }
        case 149: break;
        case 41: 
          { return operator (CTokenType.MOD_ASSIGN);
          }
        case 150: break;
        case 20: 
          { return operator (CTokenType.VBAR);
          }
        case 151: break;
        case 52: 
          { return operator (CTokenType.RSH);
          }
        case 152: break;
        case 25: 
          { return token    (CTokenType.SEMI);
          }
        case 153: break;
        case 74: 
          { return token    (CTokenType.RETURN);
          }
        case 154: break;
        case 77: 
          { return token    (CTokenType.STRUCT);
          }
        case 155: break;
        case 81: 
          { return specifier(CTokenType.DOUBLE);
          }
        case 156: break;
        case 73: 
          { return token    (CTokenType.WHILE);
          }
        case 157: break;
        case 33: 
          { return operator (CTokenType.POINTS_TO);
          }
        case 158: break;
        case 38: 
          { return string   (CTokenType.STRING, yytext());
          }
        case 159: break;
        case 24: 
          { return operator (CTokenType.COMMA);
          }
        case 160: break;
        case 7: 
          { /* Ignore */
          }
        case 161: break;
        case 32: 
          { return operator (CTokenType.MINUS_ASSIGN);
          }
        case 162: break;
        case 3: 
          { if (isTypedefName(yytext()))
								return string (CTokenType.TYPEDEF_NAME, yytext());
							  else
								return string (CTokenType.IDENTIFIER, yytext());
          }
        case 163: break;
        case 49: 
          { return operator (CTokenType.LT_EQ);
          }
        case 164: break;
        case 10: 
          { return token    (CTokenType.C_PAREN);
          }
        case 165: break;
        case 75: 
          { return specifier(CTokenType.EXTERN);
          }
        case 166: break;
        case 46: 
          { return operator (CTokenType.VBAR_VBAR);
          }
        case 167: break;
        case 50: 
          { return operator (CTokenType.LSH);
          }
        case 168: break;
        case 6: 
          { return operator (CTokenType.STAR);
          }
        case 169: break;
        case 8: 
          { return operator (CTokenType.DOT);
          }
        case 170: break;
        case 37: 
          { return token    (CTokenType.DO);
          }
        case 171: break;
        case 11: 
          { return token    (CTokenType.O_BRACK);
          }
        case 172: break;
        case 69: 
          { return token    (CTokenType.UNION);
          }
        case 173: break;
        case 60: 
          { return specifier(CTokenType.LONG);
          }
        case 174: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
              {
                return token    (CTokenType.EOF);
              }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
