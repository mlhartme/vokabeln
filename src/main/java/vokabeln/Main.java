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
import net.oneandone.inline.Cli;
import net.oneandone.sushi.fs.World;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {
	private static final String ANSI_CLS = "\u001b[2J";
	private static final String ANSI_HOME = "\u001b[H";

	public static void main(String[] args) throws IOException {
		World world;
		List<String> lines;
		Map<String, String> map;
		int idx;
		Iterator<String> iter;
		String left;
		String right;
		String input;

		if (args.length != 1) {
			throw new ArgumentException("missing file name");
		}
		world = World.create();
		lines = world.file(args[0]).readLines();
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
		for (int runde = 1; true; runde++) {
			System.out.println("Runde " + runde + ", " + map.size() + " Vokabeln");
			iter = map.keySet().iterator();
			while (iter.hasNext()) {
				left = iter.next();
				right = map.get(left);
				System.out.print(left);
				System.out.print(" = ");
				input = System.console().readLine().trim();
				if (right.equals(input)) {
					iter.remove();
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
}
