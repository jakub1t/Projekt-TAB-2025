### Projekt-TAB-2025

## Projekt z przedmiotu TAB.

---

### **Instrukcja i użyteczne komendy.**

---

## Pobranie projektu:

1. Wybierz folder w którym ma się znaleźć repozytorium.
2. Wprowadź komendę (gitbash, terminal, etc.): 

```
git clone https://github.com/jakub1t/Projekt-TAB-2025.git
```

3. Żeby pracować nad projektem wejdź do folderu projektu, na przykład komendą: 

```
cd Projekt-TAB-2025
```

---

## Praca nad projektem:

1. Zarządzanie gałęziami:

- Dodanie nowej gałęzi w lokalnym repozytorium:


```
git checkout -b nazwa-galezi
```

- Pobranie istniejącej gałęzi z GitHub:

```
git fetch origin nazwa-galezi
```

 następnie:

```
git checkout nazwa-galezi
```

- Zaktualizowanie lokalnej gałęzi z GitHub:

```
git pull origin nazwa-galezi
```

- Wyświetlenie gałęzi w lokalnym repozytorium:

```
git branch
```

- Przejście na inną, istniejącą gałąź:

```
git switch nazwa-galezi
```

- Usunięcie gałęzi w lokalnym repozytorium:

```
git branch -d nazwa-galezi
```

2. Sprawdzenie stanu plików w lokalnym repozytorium:

```
git status
```

3. Dodanie zmienionych plików:


```
git add nazwa-pliku
```

 lub aby dodać wszystko:

```
git add .
```

4. Zapisanie zmian lokalnie:

```
git commit -m "Opis zmian."
```

5. Przeglądanie commitów w lokalnym repozytorium:

```
git log
```

 z wprowadzonymi zmianami:

```
git log -p
```

6. Wysłanie zmian na GitHub:

```
git push origin nazwa-galezi
```

---

