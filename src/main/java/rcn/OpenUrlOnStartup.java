package rcn;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class OpenUrlOnStartup implements 
ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		String url = "http://localhost:8086/";
		String os = System.getProperty("os.name").toLowerCase();
		Runtime rt = Runtime.getRuntime();

		try{
			if (os.indexOf( "win" ) >= 0) {
//				rt.exec( "rundll32 url.dll,FileProtocolHandler " + url);
				System.out.println("Local website opened in default Browser!");

			} else if (os.indexOf( "mac" ) >= 0) {
				rt.exec( "open " + url);
				System.out.println("Local website opened in default Browser!");

			}
			/*else if (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0) {
				String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror", "netscape","opera","links","lynx"};

				//"browser1 "url" || browser2 "url" ||..."
				StringBuffer cmd = new StringBuffer();
				for (int i=0; i<browsers.length; i++) {
					cmd.append( (i==0  ? "" : " || " ) + browsers[i] +" \"" + url + "\" ");
				}

				rt.exec(new String[] { "sh", "-c", cmd.toString() });

			}*/
		} catch (Exception e){}
	}

}