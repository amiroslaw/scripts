import java.awt.datatransfer.StringSelection
import java.awt.Toolkit
import java.awt.datatransfer.*

class Clip{

	static final Clipboard clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard()


	static String getClipboardContents(){
		return clipBoard.getContents(null).getTransferData(DataFlavor.stringFlavor)
	}
	static void main(String [] args){
		def com
			def clip = getClipboardContents()
			// def clip = getClipboardText()
			println clip
			def arg
			if(args.length != 0){
				arg = args[0]
			}

		switch(arg){
			case "vlc":  
				com = "vlc " + clip
					break
			case "v": 
				com = "vivaldi " + clip
				break
			case "mag": 
				com = "/home/miro/Dokumenty/Ustawienia/magnet.sh " + clip
				break
			case "h": 
				println " vlc \n v → vivaldi \n man → magnet link \n default → smplayer\n"
				break
//don't work
			case "gal": 
				com = "gallery-dl " + clip
				com.waitFor()
				break
			case "yta": 
com = "nohup youtube-dl -x --audio-format mp3 \"" + clip + "\" &"
				// com = "youtube-dl -x --audio-format mp3 \"" + clip+"\""
println com
				break
			case "yt": 
				com = "youtube-dl "
				break
			default: 
				com = "smplayer " + clip

		}
		if(com != null)
			com.execute()
	}
		static void setClipboardContents(final String contents){
			clipBoard.setContents(new StringSelection(contents), null)
		}
	static String getClipboardText() {
		Transferable t = clipBoard.getContents(null);
		if (t.isDataFlavorSupported(DataFlavor.stringFlavor))
			return t.getTransferData(DataFlavor.stringFlavor)
			// System.out.println(t.getTransferData(DataFlavor.stringFlavor));
	}
}
