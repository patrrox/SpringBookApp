package ostasp.bookapp.user.application.port;

import ostasp.bookapp.commons.Either;
import ostasp.bookapp.user.domain.UserEntity;

public interface UserRegistationUseCase {

    RegisterResponse register (String username, String password);


    class RegisterResponse extends Either<String, UserEntity>{

        public RegisterResponse(boolean success, String left, UserEntity right) {
            super(success, left, right);
        }

        public static RegisterResponse success (UserEntity right){
            return new RegisterResponse(true,null,right);
        }
        public static RegisterResponse failure (String left){
            return new RegisterResponse(true,left,null);
        }

    }
}
