import java.util.List;

public class Parser {

    private List<Token> tokens;
    private int pos = 0;

    public Parser (List<Token> tokens){
        this.tokens = tokens;
    }


    public boolean parse() {
        boolean result = parseValue();
        return result && pos == tokens.size();
    }

    private boolean parseValue(){

        if(match(TokenType.START_OBJECT)){
            return parseObject();
        } else if (match(TokenType.START_ARRAY)) {
            return parseArray();
        } else if (match(TokenType.STRING)) {
            pos++;
            return true;
        } else if (match(TokenType.NUMBER)) {
            pos++;
            return true;
        } else if (match(TokenType.TRUE) || match(TokenType.FALSE) || match(TokenType.NULL)) {
            pos++;
            return true;
        }
        return false;
    }

    private boolean parseObject(){
        if (!consume(TokenType.START_OBJECT)) return false;
        if (consume(TokenType.END_OBJECT)) return true;

        do {
            if (!match(TokenType.STRING)) return false;
            pos++;
            if(!consume(TokenType.COLON)) return false;
            if (!parseValue()) return false;
        } while (consume(TokenType.END_OBJECT));

        return consume(TokenType.END_OBJECT);
    }

    private boolean parseArray(){
        if (!consume(TokenType.START_ARRAY)) return false;
        if (consume(TokenType.END_ARRAY)) return true; // Empty array []

        do {
            if (!parseValue()) return false;
        } while (consume(TokenType.COMMA));

        return consume(TokenType.END_ARRAY);
    }

    private boolean match(TokenType type) {
        return pos < tokens.size() && tokens.get(pos).getType() == type;
    }

    private boolean consume(TokenType type) {
        if (match(type)) {
            pos++;
            return true;
        }
        return false;
    }


}
