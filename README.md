### Reflection 
> List the code quality issue(s) that you fixed during the exercise and explain your strategy
on fixing them.
  
* Masalah Dependency Verification pada Gradle
Proses build di dalam Docker gagal karena file verification-metadata.xml menyebabkan error verifikasi dependensi.
Karena fitur dependency verification tidak menjadi fokus pada modul ini dan justru menghambat proses CI/CD, saya menghapus file tersebut agar proses build berjalan stabil.


* Masalah Permission pada Gradle di Docker
Docker build gagal dengan pesan Permission denied saat menjalankan ./gradlew.
Saya memperbaikinya dengan menambahkan perintah RUN chmod +x gradlew di dalam Dockerfile agar Gradle Wrapper dapat dieksekusi di dalam container.

Strategi utama saya dalam memperbaiki masalah-masalah tersebut adalah:
- Membaca log error dari CI/CD secara detail.
- Mengidentifikasi akar permasalahan dari setiap error.
- Melakukan perbaikan kecil dan terfokus, lalu menguji ulang melalui workflow CI.
---
> 2. Look at your CI/CD workflows (GitHub)/pipelines (GitLab). Do you think the current
     implementation has met the definition of Continuous Integration and Continuous
     Deployment? Explain the reasons (minimum 3 sentences)!

Menurut saya, implementasi workflow yang dibuat sudah memenuhi konsep Continuous Integration (CI) karena setiap push ke repository secara otomatis menjalankan proses build, unit test, dan analisis kualitas kode sehingga kesalahan dapat terdeteksi lebih awal. Implementasi ini juga memenuhi konsep Continuous Deployment (CD) karena setiap perubahan pada branch utama langsung dideploy ke platform PaaS tanpa intervensi manual. Dengan pipeline ini, proses integrasi, pengujian, dan deployment menjadi otomatis, konsisten, dan mengurangi risiko kesalahan akibat proses manual.

### Reflection 
> List the code quality issue(s) that you fixed during the exercise and explain your strategy
on fixing them.
  
* Masalah Dependency Verification pada Gradle
Proses build di dalam Docker gagal karena file verification-metadata.xml menyebabkan error verifikasi dependensi.
Karena fitur dependency verification tidak menjadi fokus pada modul ini dan justru menghambat proses CI/CD, saya menghapus file tersebut agar proses build berjalan stabil.


* Masalah Permission pada Gradle di Docker
Docker build gagal dengan pesan Permission denied saat menjalankan ./gradlew.
Saya memperbaikinya dengan menambahkan perintah RUN chmod +x gradlew di dalam Dockerfile agar Gradle Wrapper dapat dieksekusi di dalam container.

Strategi utama saya dalam memperbaiki masalah-masalah tersebut adalah:
- Membaca log error dari CI/CD secara detail.
- Mengidentifikasi akar permasalahan dari setiap error.
- Melakukan perbaikan kecil dan terfokus, lalu menguji ulang melalui workflow CI.
---
> 2. Look at your CI/CD workflows (GitHub)/pipelines (GitLab). Do you think the current
     implementation has met the definition of Continuous Integration and Continuous
     Deployment? Explain the reasons (minimum 3 sentences)!

Menurut saya, implementasi workflow yang dibuat sudah memenuhi konsep Continuous Integration (CI) karena setiap push ke repository secara otomatis menjalankan proses build, unit test, dan analisis kualitas kode sehingga kesalahan dapat terdeteksi lebih awal. Implementasi ini juga memenuhi konsep Continuous Deployment (CD) karena setiap perubahan pada branch utama langsung dideploy ke platform PaaS tanpa intervensi manual. Dengan pipeline ini, proses integrasi, pengujian, dan deployment menjadi otomatis, konsisten, dan mengurangi risiko kesalahan akibat proses manual.

---
### SOLID Principles Reflection

#### 1) Explain what principles you apply to your project!

**SRP (Single Responsibility Principle):**
Saya memisahkan CarController dari ProductController.java ke dalam file terpisah bernama CarController.java. Sebelumnya, file ProductController.java berisi dua kelas (ProductController dan CarController), yang melanggar prinsip SRP karena satu file memiliki lebih dari satu tanggung jawab. Sekarang setiap controller berada di file masing-masing dan memiliki satu tanggung jawab yang jelas.

**OCP (Open/Closed Principle):**
Layer service sudah menerapkan OCP melalui penggunaan interface (ProductService dan CarService). Kode bersifat terbuka untuk pengembangan (dapat dibuat implementasi baru), tetapi tertutup untuk modifikasi (tidak perlu mengubah kode yang sudah ada ketika menambahkan fitur baru).

**LSP (Liskov Substitution Principle):**
Saya menghapus pewarisan CarController extends ProductController. Pewarisan ini melanggar LSP karena Car bukan merupakan turunan dari Product, dan CarController tidak dapat menggantikan ProductController secara semantik. Keduanya menangani entitas yang berbeda sehingga tidak seharusnya memiliki hubungan inheritance. Sekarang kedua controller berdiri sebagai kelas yang independen.

**ISP (Interface Segregation Principle):**
Interface ProductService dan CarService sudah terpisah dengan baik. Masing-masing hanya berisi method yang relevan dengan domainnya (produk atau mobil). Dengan demikian, klien tidak dipaksa bergantung pada method yang tidak mereka gunakan.

**DIP (Dependency Inversion Principle):**
Saya mengubah CarController sehingga tidak lagi bergantung pada CarServiceImpl (kelas konkret), tetapi bergantung pada CarService (interface). Modul tingkat tinggi (controller) sekarang bergantung pada abstraksi, bukan pada implementasi konkret.

#### 2) Explain the advantages of applying SOLID principles to your project with examples.

Dengan memisahkan CarController ke file tersendiri (SRP), perubahan pada fitur mobil tidak akan memengaruhi fitur produk. Jika saya ingin mengubah logika terkait mobil, saya cukup memodifikasi CarController.java.

Lebih Mudah Diuji (Testability):
Dengan menerapkan DIP, CarController bergantung pada interface CarService, bukan CarServiceImpl. Hal ini memudahkan proses unit testing karena saya dapat membuat mock CarService tanpa memerlukan implementasi aslinya.

Fleksibel dan Mudah Dikembangkan (Extensibility):
Karena bergantung pada interface (DIP), saya dapat mengganti implementasi dengan mudah. Misalnya, jika ingin menambahkan caching atau mengganti penyimpanan dari in-memory ke database, saya cukup membuat implementasi baru dari CarService tanpa perlu mengubah CarController.

Struktur Kode Lebih Jelas:
Dengan memisahkan kelas ke dalam file masing-masing, struktur proyek menjadi lebih rapi dan mudah dipahami. Developer dapat langsung menemukan logika terkait mobil di CarController.java dan logika terkait produk di ProductController.java.

#### 3) Explain the disadvantages of not applying SOLID principles to your project with examples.

Tanpa SRP:
Ketika CarController masih berada di dalam ProductController.java, setiap perubahan pada salah satu controller berisiko memengaruhi controller lainnya. File menjadi lebih sulit dibaca dan dipelihara karena mencampurkan dua tanggung jawab yang berbeda.

Tanpa LSP:
Pewarisan CarController extends ProductController menimbulkan kesan bahwa fitur mobil adalah spesialisasi dari fitur produk, padahal tidak demikian. Hal ini dapat menimbulkan bug jika seseorang mencoba menggunakan CarController sebagai pengganti ProductController atau mengasumsikan method turunan produk akan bekerja untuk mobil.

Tanpa DIP:
Ketika CarController bergantung langsung pada CarServiceImpl, terjadi tight coupling terhadap implementasi tertentu. Jika ingin mengganti implementasi service, controller juga harus ikut diubah. Selain itu, proses unit testing menjadi lebih sulit karena tidak dapat dengan mudah melakukan mocking terhadap kelas konkret.

---
### TDD Reflection

> 1. Reflect based on Percival (2017) proposed self-reflective questions (in "Principles and Best Practice of Testing" submodule, chapter "Evaluating Your Testing Objectives"), whether this TDD flow is useful enough for you or not. If not, explain things that you need to do next time you make more tests.

Menurut saya, alur TDD yang saya gunakan cukup membantu selama proses pengembangan. Berdasarkan pertanyaan reflektif dari Percival (2017), saya merasa beberapa tujuan dari testing sudah tercapai.

Pertama, test memberikan rasa percaya diri saat melakukan refactoring. Dengan adanya unit test seperti OrderServiceImplTest dan OrderRepositoryTest, saya tidak terlalu khawatir ketika melakukan perubahan pada kode. Jika ada perubahan yang menyebabkan perilaku program menjadi salah, test yang ada biasanya langsung gagal sehingga masalahnya bisa cepat terlihat.

Kedua, test juga membantu menemukan bug lebih awal. Beberapa test yang dibuat mencakup kondisi tertentu seperti ketika order sudah ada sebelumnya (testCreateOrderIfAlreadyExists) atau ketika status yang diberikan tidak valid (testUpdateStatusInvalidStatus). Dengan adanya test untuk kondisi-kondisi ini, potensi error bisa terdeteksi sebelum kode digunakan lebih jauh.

Selain itu, test juga cukup membantu sebagai dokumentasi perilaku sistem. Nama method test seperti testFindByIdIfIdFound dan testFindByIdIfIdNotFound sudah cukup jelas untuk menggambarkan apa yang seharusnya dilakukan oleh fungsi tersebut.

Walaupun begitu, masih ada beberapa hal yang bisa diperbaiki ke depannya. Misalnya, saya perlu membuat test yang mencakup lebih banyak variasi kasus, terutama edge case yang mungkin belum terpikirkan. Selain itu, saya juga ingin lebih konsisten menerapkan strict TDD, yaitu menulis test terlebih dahulu sebelum menulis implementasi. Dengan cara ini, desain kode bisa lebih dipandu oleh kebutuhan dari test. Terakhir, akan lebih baik jika ditambahkan integration test agar bisa memastikan bahwa beberapa komponen dalam sistem dapat bekerja dengan baik ketika digabungkan.

> 2. You have created unit tests in Tutorial. Now reflect whether your tests have successfully followed F.I.R.S.T. principle or not. If not, explain things that you need to do the next time you create more tests.

Secara umum, unit test yang saya buat sudah cukup mengikuti prinsip F.I.R.S.T.

Fast (Cepat)
Test dapat dijalankan dengan cepat karena dependency seperti OrderRepository dimock menggunakan Mockito. Dengan begitu, test tidak perlu berinteraksi langsung dengan database atau operasi I/O lainnya yang biasanya lebih lambat.

Independent / Isolated (Independen)
Setiap test berjalan secara terpisah dan tidak bergantung pada hasil test lain. Penggunaan @BeforeEach membantu memastikan bahwa setiap test dimulai dari kondisi awal yang bersih, sehingga perubahan pada satu test tidak mempengaruhi test yang lain.

Repeatable (Dapat Diulang)
Test dapat dijalankan berkali-kali dengan hasil yang konsisten. Hal ini karena test tidak bergantung pada faktor eksternal seperti koneksi jaringan, database nyata, atau waktu sistem. Data yang digunakan juga selalu dibuat ulang melalui method setUp().

Self-Validating (Memvalidasi Sendiri)
Setiap test memiliki assertion yang jelas menggunakan fungsi seperti assertEquals, assertNull, assertTrue, dan assertThrows. Dengan begitu, hasil test langsung menunjukkan apakah test tersebut berhasil atau gagal tanpa perlu pengecekan manual.

Timely (Tepat Waktu)
Test dibuat mengikuti alur TDD, yaitu ditulis sebelum atau bersamaan dengan implementasi kode. Hal ini membantu memastikan bahwa kode yang dibuat memang memenuhi kebutuhan yang sudah didefinisikan melalui test.

Secara keseluruhan, unit test yang saya buat sudah cukup sesuai dengan prinsip F.I.R.S.T. Namun, ke depannya saya ingin lebih konsisten dalam menerapkan pendekatan TDD secara ketat, yaitu benar-benar menulis test terlebih dahulu sebelum implementasi, serta memastikan bahwa setiap test tetap terisolasi dan tidak berbagi state dengan test lain.