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
		Map<String, String> all;
		String left;
		String right;
		String input;
		List<String> round;
		int roundCount;
		int tries;

		Terminal terminal = TerminalBuilder.terminal();
		LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).build();
		if (args.length == 0) {
			throw new ArgumentException("usage: vokabeln file+");
		}

		all = load(args);
		for (int runde = 1; true; runde++) {
			round = new ArrayList<>(all.keySet());
			roundCount = round.size();
			while (!round.isEmpty()) {
				left = eatRandomKey(round);
				right = all.get(left);
				tries = 0;
				while (true) {
					tries++;
					cls();
					System.out.println("Runde " + runde + ", Vokabel " + (roundCount - round.size()) + "/" + roundCount);
					input = lineReader.readLine(left + " = ").trim();
					if (right.equals(input)) {
						if (tries == 1) {
							all.remove(left);
						}
						System.out.println("  richtig :)");
						System.console().readLine();
						break;
					} else {
						System.out.println("  " + left + " = " + right);
						System.out.println("  noch nicht richtig ...");
						System.console().readLine();
					}
				}
			}
			System.out.println();
			if (all.isEmpty()) {
				System.out.println("Geschafft :) Zahl der Runden: " + runde);
				break;
			}
			System.out.println("Runde beendet - bitte Return drücken");
			System.console().readLine();
		}
	}

	private static void cls() {
		System.out.println(ANSI_CLS + ANSI_HOME);
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
