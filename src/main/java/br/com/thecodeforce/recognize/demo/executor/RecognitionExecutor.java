package br.com.thecodeforce.recognize.demo.executor;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;


import br.com.thecodeforce.recognize.demo.helper.ThrowingFunction;
import br.com.thecodeforce.recognize.demo.model.TranscriptionDTO;
import reactor.core.publisher.Mono;

@Service
public class RecognitionExecutor {

	@Value("${app.google.apiKey}")
	private String apiKey;

	public Mono<TranscriptionDTO> execute(Mono<FilePart> voiceData) {
		return voiceData.flatMapMany(FilePart::content)
				.map(ThrowingFunction.unchecked(dbf -> dbf.asByteBuffer().array())).reduce(new byte[0], (in, b) -> {
					byte result[] = Arrays.copyOf(in, in.length + b.length);
					System.arraycopy(b, 0, result, in.length, b.length);
					return result;
				}).map(ThrowingFunction.unchecked(dataStream -> {
					
					var file = Files.createTempFile("google-", "-voice");
					var fos = new FileOutputStream(file.toFile());
					IOUtils.write(dataStream, fos);
					IOUtils.closeQuietly(fos);
					
					var recognizer = new Recognizer(Recognizer.Languages.PORTUGUESE_BRASIL, apiKey);
					
					var response = recognizer.getRecognizedDataForFlac(file.toFile(), 2);
					
					return new TranscriptionDTO();
				}));
	}

}
