---?color=linear-gradient(180deg, white 75%, black 25%)
@title[Title slide]
@snap[midpoint span-100 h1]
## Strong-typed functions in Scala
@snapend

@snap[south span-100 text-white small]
com.dreamlines.connectivity
03.11.2019
@snapend

---?color=linear-gradient(90deg, white 35%, #5384AD 65%)
@title[Agenda]
@snap[west-north span-40 h3]
## Agenda
@snapend

@snap[west-south span-50]
![IMAGE](assets/img/presentation.png)
@snapend

@snap[east span-100]
@ul[list-spaced-bullets text-white text-09]
- Problem
- Alternatives
    - Value classes
    - @newrecord
    - Refined
    - Enforcing types
- Examples: GreenGinza
- Conclusions
    
@ulend
@snapend

---?color=linear-gradient(90deg, #5384AD 35%, white 65%)
@title[Problem]

@snap[north-east h4-black]
## Introduction
@snapend

@snap[west span-50]
One of the biggest benefits FP is that it lets us reason about functions by looking at their *type signature*.
However...
@snapend

@snap[east span-50 black]
**We often end up with weakly-typed functions**
@snapend

---

@snap[north-east span-100 text-pink text-06]
Let your code do the talking!
@snapend

```sql zoom-18
CREATE TABLE "topic" (
    "id" serial NOT NULL PRIMARY KEY,
    "forum_id" integer NOT NULL,
    "subject" varchar(255) NOT NULL
);
ALTER TABLE "topic"
ADD CONSTRAINT forum_id
FOREIGN KEY ("forum_id")
REFERENCES "forum" ("id");
```

@snap[south span-100 text-gray text-08]
@[1-5](You can step-and-ZOOM into fenced-code blocks, source files, and Github GIST.)
@[6,7, zoom-13](Using GitPitch live code presenting with optional annotations.)
@[8-9, zoom-12](This means no more switching between your slide deck and IDE on stage.)
@snapend


---?image=assets/img/presenter.jpg

@snap[north span-100 h2-white]
## Now It's Your Turn
@snapend

@snap[south span-100 text-06]
[Click here to jump straight into the interactive feature guides in the GitPitch Docs @fa[external-link]](https://gitpitch.com/docs/getting-started/tutorial/)
@snapend
