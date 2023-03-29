package backEnd;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.locks.ReentrantLock;

public class Wallet {
    /**
     * The RandomAccessFile of the wallet file
     */  
    private RandomAccessFile file;
    
    private ReentrantLock lock = new ReentrantLock();

    /**
     * Creates a Wallet object
     *
     * A Wallet object interfaces with the wallet RandomAccessFile
     */
    public Wallet () throws Exception {
	this.file = new RandomAccessFile(new File("backEnd/wallet.txt"), "rw");
    }

    public boolean safeWithdraw(int valueToWithdraw) throws Exception {
        return (getBalance()-valueToWithdraw) >= 0;
    }

    /**
     * Gets the wallet balance. 
     *
     * @return                   The content of the wallet file as an integer
     */
    public int getBalance() throws IOException {
	this.file.seek(0);
	return Integer.parseInt(this.file.readLine());
    }

    /**
     * Sets a new balance in the wallet
     *
     * @param  newBalance          new balance to write in the wallet
     */
    private void setBalance(int newBalance) throws Exception {
	this.file.setLength(0);
	String str = Integer.valueOf(newBalance).toString()+'\n'; 
	this.file.writeBytes(str); 
    }

    /**
     * Closes the RandomAccessFile in this.file
     */
    public void close() throws Exception {
	this.file.close();
    }

    public boolean buy(String prod, Wallet wallet, Pocket pocket) throws Exception {
        int prodCost = Store.getProductPrice(prod); 
        lock.lock();
        int bal = getBalance();
        if (prodCost > bal) {
            lock.unlock();
            return false;
        }
        int newBal = bal - prodCost; 
        setBalance(newBal);
        pocket.addProduct(prod);
        lock.unlock();
        return true;
    }

}
