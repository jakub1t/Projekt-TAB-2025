### TAB-sem6-2025-s21

## Projekt z przedmiotu TAB.

---

### Instrukcja i użyteczne komendy:

---

## Pobranie projektu:

1. Wybierz folder w którym ma się znaleźć repozytorium.
2. Wprowadź komendę (gitbash, terminal, etc.): 

```
git clone https://github.com/jakub1t/TAB-sem6-2025-s21.git
```

3. Żeby pracować nad projektem wejdź do folderu projektu, na przykład komendą: 

```
cd TAB-sem6-2025-s21
```

 w celu sprawdzenia można wpisać:

```
git status
```
---

## Praca nad projektem:

1. Dodanie nowej gałęzi w lokalnym repozytorium:


```
git checkout -b nazwa-galezi
```

2. Pobranie istniejącej gałęzi z GitHub:

```
git fetch origin nazwa-galezi
```

 następnie:

```
git checkout nazwa-galezi
```

3. Wyświetlenie gałęzi w lokalnym repozytorium:

```
git branch
```

4. Przejście na inną, istniejącą gałąź:

```
git switch nazwa-galezi
```

5. Usunięcie gałęzi w lokalnym repozytorium:

```
git branch -d nazwa-galezi
```

6. Sprawdzenie stanu plików w lokalnym repozytorium:

```
git status
```

7. Dodanie zmienionych plików:


```
git add nazwa-pliku
```

 lub aby dodać wszystko:

```
git add .
```

8. Zapisanie zmian lokalnie:

```
git commit -m "Opis zmian."
```

9. Przeglądanie commitów w lokalnym repozytorium:

```
git log
```

 z wprowadzonymi zmianami:

```
git log -p
```

10. Wysłanie zmian na GitHub:

```
git push origin nazwa-galezi
```
---

