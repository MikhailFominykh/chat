package test.chat.server;

public interface ClientRegistry<T> {

	public void addClient(Client<T> client);

	public void removeClient(Client<T> client);

}
