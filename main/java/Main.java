import dev.langchain4j.model.openai.OpenAiChatModel;

public class Main {
    public static void main(String[] args) {
        OpenAiChatModel model = OpenAiChatModel.withApiKey("demo");
        String answer = model.generate("Say 'Hello World'");
        System.out.println(answer); // Hello World
    }
}