/*
 * Copyright Michael Hartmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vokabeln;

import net.oneandone.inline.ArgumentException;
import net.oneandone.sushi.fs.World;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {
	// https://en.wikipedia.org/wiki/ANSI_escape_code
	private static final String ANSI_CLS = "\u001b[2J\u001b[3J";
	private static final String ANSI_HOME = "\u001b[H";

	public static void main(String[] args) throws IOException {
		Map<String, String> map;
		String left;
		String right;
		String input;
		List<String> keys;

		Terminal terminal = TerminalBuilder.terminal();
		LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).build();
		if (args.length == 0) {
			throw new ArgumentException("usage: vokabeln file+");
		}

		map = load(args);
		for (int runde = 1; true; runde++) {
			System.out.println("Runde " + runde + ", " + map.size() + " Vokabeln");
			keys = new ArrayList<>(map.keySet());
			while (!keys.isEmpty()) {
				left = eatRandomKey(keys);
				right = map.get(left);
				input = lineReader.readLine(left + " = ").trim();
				if (right.equals(input)) {
					map.remove(left);
				} else {
					System.out.println("  stimmt nicht, richtig ist:");
					System.out.println("  " + left + " = " + right);
				}
			}
			System.out.println();
			if (map.isEmpty()) {
				System.out.println("Geschafft :) Zahl der Runden: " + runde);
				break;
			}
			System.out.println("Runde beendet - bitte Return drücken");
			System.console().readLine();
			System.out.println(ANSI_CLS + ANSI_HOME);
		}
	}

	private static Map<String, String> load(String ... files) throws IOException {
		World world;
		List<String> lines;
		Map<String, String> map;
		int idx;

		world = World.create();
		lines = new ArrayList<>();
		for (String file : files) {
			lines.addAll(world.file(file).readLines());
		}
		map = new LinkedHashMap<>();
		for (String line : lines) {
			if (line.trim().isEmpty()) {
				continue;
			}
			idx = line.indexOf('=');
			if (idx == -1) {
				System.out.println("syntax error: " + line);
			}
			map.put(line.substring(0, idx).trim(), line.substring(idx + 1).trim());
		}
		return map;
	}

	private static final Random random = new Random();

	private static String eatRandomKey(List<String> entries) {
		int idx;

		idx = random.nextInt(entries.size());
		return entries.remove(idx);
	}
}
