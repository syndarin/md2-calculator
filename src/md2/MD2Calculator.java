package md2;

public class MD2Calculator {
	
	// описание алгоритма
	// http://ru.wikipedia.org/wiki/MD2
	
	private final int HASH_LENGHT=16;
	private final int MD_BLOCK_LENGTH=48;
	private final int HASH_ROUNDS=18;
	
	private final int[] PI={
			  41,  46,  67, 201, 162, 216, 124,   1,  61,  54,  84, 161, 236, 240,   6,  19,
			  98, 167,   5, 243, 192, 199, 115, 140, 152, 147,  43, 217, 188,  76, 130, 202,
			  30, 155,  87,  60, 253, 212, 224,  22, 103,  66, 111,  24, 138,  23, 229,  18,
			 190,  78, 196, 214, 218, 158, 222,  73, 160, 251, 245, 142, 187,  47, 238, 122,
			 169, 104, 121, 145,  21, 178,   7,  63, 148, 194,  16, 137,  11,  34,  95,  33,
			 128, 127,  93, 154,  90, 144,  50,  39,  53,  62, 204, 231, 191, 247, 151,   3,
			 255,  25,  48, 179,  72, 165, 181, 209, 215,  94, 146,  42, 172,  86, 170, 198,
			  79, 184,  56, 210, 150, 164, 125, 182, 118, 252, 107, 226, 156, 116,   4, 241,
			  69, 157, 112,  89, 100, 113, 135,  32, 134,  91, 207, 101, 230,  45, 168,   2,
			  27,  96,  37, 173, 174, 176, 185, 246,  28,  70,  97, 105,  52,  64, 126,  15,
			  85,  71, 163,  35, 221,  81, 175,  58, 195,  92, 249, 206, 186, 197, 234,  38,
			  44,  83,  13, 110, 133,  40, 132,   9, 211, 223, 205, 244,  65, 129,  77,  82,
			 106, 220,  55, 200, 108, 193, 171, 250,  36, 225, 123,   8,  12, 189, 177,  74,
			 120, 136, 149, 139, 227,  99, 232, 109, 233, 203, 213, 254,  59,   0,  29,  57,
			 242, 239, 183,  14, 102,  88, 208, 228, 166, 119, 114, 248, 235, 117,  75,  10,
			  49,  68,  80, 180, 143, 237,  31,  26, 219, 153, 141,  51, 159,  17, 131,  20};

	public String calculate(String message){
		
		// берем байты исходного сообщения
		byte[] messageBytes=message.getBytes();
		
		// считаем количество байт, необходимых для дополнения ссобщения
		// чтобы его размер стал кратен 16
		int addBytesCount=HASH_LENGHT-(messageBytes.length%HASH_LENGHT);
		
		// рабочий блок байт, кратный 16
		byte[] workBlock=new byte[messageBytes.length+addBytesCount];
		
		// заполняем его исходными байтами сообщения
		for(int i=0; i<messageBytes.length; i++){
			workBlock[i]=messageBytes[i];
		}
		
		// считаем контрольную сумму от рабочего блока
		byte[] checksum=generateChecksum(workBlock);
		
		// байтовый массив с контрольной суммой
		byte[] blockWithChecksum=new byte[workBlock.length+checksum.length];
		
		// заполняем его значениями сообщения...
		for(int i=0; i<workBlock.length; i++){
			blockWithChecksum[i]=workBlock[i];
		}
		
		// ... и прибавляем контрольную сумму в "хвост"
		for(int i=workBlock.length; i<blockWithChecksum.length; i++){
			blockWithChecksum[i]=checksum[i-workBlock.length];
		}
		
		// MD блок
		byte[] xBuffer=new byte[MD_BLOCK_LENGTH];
		
		for(int i=0; i<blockWithChecksum.length/HASH_LENGHT; i++){
			
			// 1-е 16 байт оставляем равными 0
			// заполняем 2е 16 байт 16ю байтами сообщения, а
			// 3и 16 байт - ксором 1х и 2х 16-ти байтных блоков
			for(int j=0; j<HASH_LENGHT; j++){
				xBuffer[16+j]=blockWithChecksum[i*16+j];
				xBuffer[32+j]=Integer.valueOf(xBuffer[16+j]^xBuffer[j]).byteValue();
			}
			
			byte t=0;
			
			// производим 18 раундов хэширования
			for(int j=0; j<HASH_ROUNDS; j++){
				
				for(int k=0; k<MD_BLOCK_LENGTH; k++){
					System.out.println("hash "+k+" "+t);//***************************
					t=xBuffer[k]=Integer.valueOf(xBuffer[k]^PI[t]).byteValue();
					if(t<0){
						t<<=1;
						t>>>=1;
					}
					if(xBuffer[k]<0){
						xBuffer[k]<<=1;
						xBuffer[k]>>>=1;
					}
				}
				
				t=Integer.valueOf(((t+j)%256)).byteValue();
				if(t<0){
					t<<=1;
					t>>>=1;
				}
			}
		}
		
		StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < HASH_LENGHT; i++) {
        	
            hexString.append(String.format("%02x", (0xFF & xBuffer[i])));
        }
		
		return hexString.toString();
	}
	
	private byte[] generateChecksum(byte[] source){
		byte[] checksum=new byte[HASH_LENGHT];
		byte l=0;
		
		for(int i=0; i<source.length/HASH_LENGHT; i++){
			for(int j=0; j<HASH_LENGHT-1; j++){
				
				byte c=source[i*HASH_LENGHT+j];
				System.out.println("checksum "+c+" "+j);//***************************
				checksum[j]=Integer.valueOf(checksum[j]^PI[c^l]).byteValue();
				if(checksum[j]<0){
					checksum[j]<<=1;
					checksum[j]>>>=1;
				}
				l=checksum[j];
				
			}
		}
		return checksum;
	}
	
}
