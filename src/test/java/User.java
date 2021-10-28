import java.util.List;

public class User {

    public String userId;
    public String username;
    public List<UsersBook> books;

    public User() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<UsersBook> getBooks() {
        return books;
    }

    public void setBooks(List<UsersBook> books) {
        this.books = books;
    }

    public User(String userId, String username, List<UsersBook> books) {
        this.userId = userId;
        this.username = username;
        this.books = books;
    }


}

