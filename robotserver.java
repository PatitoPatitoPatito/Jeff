String clientSentence;
ServerSocket welcomeSocket = new ServerSocket(42069);

while (true) {
	Socket connectionSocket = welcomeSocket.accept();
	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
	clientSentence = inFromClient.readLine();
	Global.Values.Angle   = Float.parseFloat(clientSentence.split(",")[0]);
	Global.Values.Distance = Float.parseFloat(clientSentence.split(",")[1]);
}
