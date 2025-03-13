package Domain.Validator;

import Domain.User;

public class UserValidator implements Validator<User> {
    public UserValidator() {}

    @Override
    public void validate(User user) throws ValidationException {
        if (user.getFirstName().equals("")) {
            throw new ValidationException("User Fisrt Name cannot be empty");
        }
        if (user.getLastName().equals("")) {
            throw new ValidationException("User Last Name cannot be empty");
        }
    }
}
