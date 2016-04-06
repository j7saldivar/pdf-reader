/**
 * @author jorge.saldivar
 *
 */

package com.saldivar.pdfread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.saldivar.beans.Person;
import com.saldivar.dao.PersonDao;

public class PDFBoxReader {

	private static final Logger LOGGER = Logger.getLogger(PDFBoxReader.class
			.getName());

	private PDFBoxReader() {
	}

	public static void main(String[] args) throws IOException {

		ApplicationContext context = new ClassPathXmlApplicationContext(
				"config.xml");

		ClassLoader classLoader = PDFBoxReader.class.getClassLoader();

		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
				classLoader);
		Resource[] resources = resolver.getResources("classpath*:/*.pdf");

		for (Resource resource : resources) {
			LOGGER.info(resource.getFilename());

			File file = new File(classLoader
					.getResource(resource.getFilename()).getFile());

			processIdNumbers(context, file);

		}

		((ClassPathXmlApplicationContext) context).close();

	}

	public static void processIdNumbers(ApplicationContext context,
			File file) throws IOException {

		PDDocument pdDocument = PDDocument.load(file);
		PDFTextStripper textStripper = new PDFTextStripper();
		String content = textStripper.getText(pdDocument);

		String[] lines = content.split(System.getProperty("line.separator"));
		ArrayList<String> idNumbers = new ArrayList<>();
		ArrayList<String> idNumbersFetch = new ArrayList<>();
		String id = "ID";
		boolean isIdNum = false;
		List<Person> allPeople = new ArrayList<>();
		PersonDao personDao = (PersonDao) context.getBean("personDao");
		List<Person> people;

		for (String line : lines) {

			if (isIdNum) {
				idNumbers.add(line);
				idNumbersFetch.add(line);
				isIdNum = false;
			}

			if (line.equals(id)) {

				isIdNum = true;
			}

			if (idNumbersFetch.size() == 999) {
				people = personDao.search(idNumbersFetch);
				allPeople.addAll(people);
				idNumbersFetch.clear();
			}

		}

		people = personDao.search(idNumbersFetch);
		allPeople.addAll(people);

		LOGGER.info("Total Billing numbers: " + idNumbers.size());

		for (Person person : allPeople) {
			LOGGER.info("Person id: " + person);
		}

	}

}