package api.gibat.uz.exception.exps;

public class ConfirmCodeExpiredException extends RuntimeException {
  public ConfirmCodeExpiredException(String message) {
    super(message);
  }
}
