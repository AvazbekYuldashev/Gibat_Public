package api.gibat.uz.exception.exps;

public class AttemptLimitException extends RuntimeException {
    public AttemptLimitException(String message) {
        super(message);
    }
}
