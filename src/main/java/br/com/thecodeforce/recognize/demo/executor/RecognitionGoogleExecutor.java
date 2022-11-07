package br.com.thecodeforce.recognize.demo.executor;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

import br.com.thecodeforce.recognize.demo.helper.ThrowingFunction;
import br.com.thecodeforce.recognize.demo.model.TranscriptionDTO;
import reactor.core.publisher.Mono;

@Service
public class RecognitionGoogleExecutor {

	@Value("${app.google.apiKey}")
	private String apiKey;

	public Mono<TranscriptionDTO> execute(Mono<FilePart> voiceData) {
		return voiceData.flatMapMany(FilePart::content)
				.map(ThrowingFunction.unchecked(dbf -> dbf.asByteBuffer().array())).reduce(new byte[0], (in, b) -> {
					byte result[] = Arrays.copyOf(in, in.length + b.length);
					System.arraycopy(b, 0, result, in.length, b.length);
					return result;
				}).map(ThrowingFunction.unchecked(dataStream -> {

					try (SpeechClient speechClient = SpeechClient.create()) {

						// Builds the sync recognize request
						RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.FLAC)
								.setSampleRateHertz(16000).setLanguageCode("pt-BR").setEnableWordTimeOffsets(false)
								.build();
						RecognitionAudio audio = RecognitionAudio.newBuilder()
								.setContent(ByteString.copyFrom(dataStream)).build();

						// Performs speech recognition on the audio file
						RecognizeResponse response = speechClient.recognize(config, audio);
						List<SpeechRecognitionResult> results = response.getResultsList();

						for (SpeechRecognitionResult result : results) {
							// There can be several alternative transcripts for a given chunk of speech.
							// Just use the
							// first (most likely) one here.
							SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
							System.out.printf("Transcription: %s%n", alternative.getTranscript());
						}
						var transcription = new TranscriptionDTO();
						if (!results.isEmpty() && !results.get(0).getAlternativesList().isEmpty()) {
							transcription.setMessage(results.get(0).getAlternatives(0).getTranscript());
						}
						return transcription;
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}

				}));
	}

}
