package Domain.Validator;

public interface Validator<T>{
    void validate(T entity) throws ValidationException;
}
