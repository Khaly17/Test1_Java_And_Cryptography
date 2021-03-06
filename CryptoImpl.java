import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.JFileChooser;

public class CryptoImpl implements Icrypto {
    
        private Cipher c;
        private CipherInputStream cis;
        public static String fileToEncrypt;
        public static String keyFile;

        @Override
        public SecretKey genKey() {
            
            try {
                KeyGenerator gen = KeyGenerator.getInstance(ALGO);
                gen.init(KEYSIZE);
                SecretKey key = gen.generateKey();
                return key;
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return null;
        }


        @Override
        public String saveKey(SecretKey key, String keyfile) {
            try {
                FileOutputStream fos = new FileOutputStream(keyfile);
                ObjectOutputStream obj = new ObjectOutputStream(fos);
                obj.writeObject(key);
                obj.close();
                fos.close();
                return keyfile;
               } catch (Exception e) {
                   e.printStackTrace();
               }
            return null;
        }

        @Override
        public SecretKey getKey(String keyfile) {
            try {
                FileInputStream fis = new FileInputStream(keyfile);
                
                try {
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    SecretKey key = (SecretKey) ois.readObject();

                    return key;
                } catch (Exception e) {
                    e.printStackTrace();
                }   
            } catch (FileNotFoundException e) {
                
                e.printStackTrace();
            }
            return null;
        }

        //CHIFFREMENT

        //transform: "AES/CBC/PKCS5Padding"
         //Cipher.ENCRYPT_MODE
        /**
         * 
         * @param keyfile
         * @param mode
         * @return
         */
        public Cipher getCipher(String keyfile, int mode){
            try {

                c = Cipher.getInstance(TRANSFORM);
                IvParameterSpec iv = new IvParameterSpec(IV);
                c.init(mode,getKey(keyfile), iv);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return c;
        }
        /**
         * 
         * @param fileToencrypt :le chemin du fichier a chiffrer
         * @param keyfile :le chemin de la cle de chiffrement
         */
        public void Chiffrement(String fileToencrypt,String keyfile){
            try {

                FileInputStream fis = new FileInputStream(fileToencrypt);
                cis = new CipherInputStream(fis, getCipher(keyfile,Cipher.ENCRYPT_MODE));
                FileOutputStream fos = new FileOutputStream(fileToencrypt+".khd");//+".khd"

                // ObjectOutputStream obj = new ObjectOutputStream(fos);
                // obj.writeObject(fileToencrypt);
                byte[] buffer = new byte[1024];
                int nbBytesLu=0;

                while ((nbBytesLu = cis.read(buffer)) != -1){
                     fos.write(buffer, 0, nbBytesLu);
               }
               //obj.close();
               fis.close();
               fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(fileToEncrypt);
        }
        /**
         * 
         * @param fileTodecrypt : le chemin du fichier a dechiffrer
         * @param keyfile : le chemin de la cle de dechiffrement
         */
        public void Dechiffrement(String fileTodecrypt,String keyfile){
            try {

                FileInputStream fis = new FileInputStream(fileTodecrypt);
                FileOutputStream fos = new FileOutputStream(fileTodecrypt.substring(0, fileTodecrypt.length()-4));
                CipherOutputStream cos = new CipherOutputStream(fos, getCipher(keyfile, Cipher.DECRYPT_MODE));

                byte[] buffer = new byte[1024];
                int nbBytesLu=0;

                while ((nbBytesLu = fis.read(buffer)) != -1){
                     fos.write(buffer, 0, nbBytesLu);
               }
               cos.close();
               fis.close();
               fos.close();
               System.out.println("Decryption succeed!!!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(fileTodecrypt);
        }


    JFileChooser jfc = new JFileChooser();
/**
 * Cette methode sert a sauvegarder la cle 
 * @param key : la cle 
 */
    public void showSaveDg( SecretKey key){
        int option = jfc.showSaveDialog(jfc);


        if(JFileChooser.APPROVE_OPTION==option){
            String filePath = jfc.getSelectedFile().getAbsolutePath();
            this.saveKey(key, filePath);
        }
        String encoding = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println(encoding);
    }
/**
 * Cette methode sert a selectionner l'endroit ou vous voulez sauvegarder le fichier chiffrer
 * @param fileToencrypt :le chemin du fichier  a chiffrer
 */
    public void showSavechiffremnt( String fileToencrypt){
        int option = jfc.showSaveDialog(jfc);


        if(JFileChooser.APPROVE_OPTION==option){
            String filePath = jfc.getSelectedFile().getAbsolutePath();
            this.Chiffrement(fileToencrypt, filePath);
        }
        //String encoding = Base64.getEncoder().encodeToString(fileToencrypt.getBytes(fil));
       // System.out.println(encoding);
    }
/**
 * 
 * @param key : la cle de dechiffrement
 * @return : le chemin de la cle de dechiffrement
 */
    public String showOpenKeyFile(SecretKey key){
        int option = jfc.showOpenDialog(jfc);
        if(JFileChooser.APPROVE_OPTION==option){
            String filePath = jfc.getSelectedFile().getAbsolutePath();
            key= this.getKey(filePath);
            keyFile = filePath;
        }
        String encoding2 = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println(encoding2);
        return keyFile;
    }
    /**
     * Cette methode permet de choisir le chemin du fichier a chiffrer
     * @return : le chemin  du fichier a chiffrer
     */
    public String showOpenFileToEncrypt(){

        int option = jfc.showOpenDialog(jfc);
        if(JFileChooser.APPROVE_OPTION==option){
            String filePath = jfc.getSelectedFile().getAbsolutePath();
            fileToEncrypt = filePath;
        }

        return fileToEncrypt;
    }
    /**
     * cette methode fait appel a la methode showOpenFileToEncrypt()
     * @return : le chemin  du fichier a chiffrer
     */
    public String showOpenFileToDecrypt(){
        return showOpenFileToEncrypt();
    }
    /**
     * 
     * @param key : la cle
     */
    public void affichage(SecretKey key){
        JFileChooser jfc = new JFileChooser();
        int option = jfc.showOpenDialog(jfc);
        if(JFileChooser.APPROVE_OPTION==option){
            String filePath = jfc.getSelectedFile().getAbsolutePath();
            this.saveKey(key, filePath);
        }
        String encoding = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println(encoding);
    }
   

    //Dechiffrement a revoir avant de faire quoi ce soit Merci Khaly
   
} 
      


