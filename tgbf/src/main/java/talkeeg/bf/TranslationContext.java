/*
 * Copyright (c) 2014, wayerr (radiofun@ya.ru).
 *
 *     This file is part of talkeeg.
 *
 *     talkeeg is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     talkeeg is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with talkeeg.  If not, see <http://www.gnu.org/licenses/>.
 */

package talkeeg.bf;

import talkeeg.bf.schema.SchemaEntry;

/**
 * Contex of {@link talkeeg.bf.Translator }
 * Created by wayerr on 17.11.14.
 */
public interface TranslationContext {
    /**
     * translator for specified schema entry <p/>
     * If context does not have appropriate translator, then error will be thrown
     * @param schemaEntry
     * @return translator
     */
    Translator getTranslator(SchemaEntry schemaEntry);
}
